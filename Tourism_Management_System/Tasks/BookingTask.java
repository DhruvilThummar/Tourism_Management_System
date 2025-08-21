package src.Tourism_Management_System.Tasks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import src.Tourism_Management_System.ColorCodes;
import src.Tourism_Management_System.Tour_Info.Tour_Info;
import src.Tourism_Management_System.User_Info.Customer;
import src.Tourism_Management_System.User_Info.TravelAgent;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID; // For generating booking_id if not AUTO_INCREMENT

public class BookingTask implements Runnable {
    private Tour_Info tour;
    private Customer customer;
    private TravelAgent agent; // Can be null if customer books directly
    private Connection con;
    private String bookingType; // "customer" or "agent"

    public BookingTask(Tour_Info tour, Customer customer, Connection con, String bookingType) {
        this.tour = tour;
        this.customer = customer;
        this.con = con;
        this.bookingType = bookingType;
        this.agent = null; // Default to null for customer direct booking
    }

    public BookingTask(Tour_Info tour, Customer customer, TravelAgent agent, Connection con, String bookingType) {
        this.tour = tour;
        this.customer = customer;
        this.agent = agent;
        this.con = con;
        this.bookingType = bookingType;
    }

    @Override
    public void run() {
        System.out.println(ColorCodes.CYAN + "Processing booking for " + customer.getUsername() + " on tour " + tour.getTourName() + " in a separate thread..." + ColorCodes.RESET);
        try {

            if (tour.bookTour(con)) { // This method decrements available slots and updates the DB
                String bookingSql;
                if ("customer".equals(bookingType)) {
                    bookingSql = "INSERT INTO Bookings (tour_id, customer_id, booking_date, status) VALUES (?, ?, CURDATE(), 'Confirmed')";
                } else { // bookingType is "agent"
                    bookingSql = "INSERT INTO Bookings (tour_id, customer_id, agent_id, booking_date, status) VALUES (?, ?, ?, CURDATE(), 'Confirmed')";
                }

                try (PreparedStatement bookingPst = con.prepareStatement(bookingSql)) {
                    bookingPst.setString(1, tour.getTourId());
                    bookingPst.setString(2, customer.getCustomerId());
                    if ("agent".equals(bookingType)) {
                        bookingPst.setString(3, agent.getAgentId());
                    }
                    bookingPst.executeUpdate();
                    System.out.println(ColorCodes.GREEN + "Tour '" + tour.getTourName() + "' booked successfully for customer " + customer.getUsername() + "!" + ColorCodes.RESET);
                    saveTicketToFile(tour, customer, agent);
                }
            } else {
                System.out.println(ColorCodes.RED + "Failed to book tour '" + tour.getTourName() + "'. No slots available or another error occurred." + ColorCodes.RESET);
            }
        } catch (SQLException e) {
            System.err.println(ColorCodes.RED + "Database error during async tour booking for " + customer.getUsername() + ": " + e.getMessage() + ColorCodes.RESET);
        } catch (IOException e) {
            System.err.println(ColorCodes.RED + "Error saving ticket to file for " + customer.getUsername() + ": " + e.getMessage() + ColorCodes.RESET);
        }
    }

    private void saveTicketToFile(Tour_Info tour, Customer customer, TravelAgent agent) throws IOException {
        String folderName = "tickets";
        File folder = new File(folderName);
        if (!folder.exists()) {
            folder.mkdir(); // Creates the directory if it doesn't exist
            System.out.println(ColorCodes.YELLOW + "Created directory: " + folderName + ColorCodes.RESET);
        }

        // Use a more detailed timestamp for unique filenames if multiple bookings happen at the same second
        DateTimeFormatter fileTimestampFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")); // Date only for part of filename
        // A more unique filename can include a portion of UUID as well
        String uniqueId = UUID.randomUUID().toString().substring(0, 8); // Short UUID part
        String fileName = String.format("%s/%s_%s_%s_%s_ticket.txt", folderName, customer.getUsername(), tour.getTourId(), timestamp, uniqueId);

        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write("====================================================\n");
            writer.write("          TOUR BOOKING TICKET          \n" );
            writer.write("====================================================\n");
            writer.write( "Booking Date: " + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "\n");
            writer.write( "----------------------------------------------------\n" );
            writer.write( "Customer Details:\n");
            writer.write( "  Username: " + customer.getUsername() + "\n" );
            writer.write("  Name:     " + (customer.getCustomerName() != null ? customer.getCustomerName() : "N/A") + "\n" );
            writer.write("  Email:    " + (customer.getEmail() != null ? customer.getEmail() : "N/A") + "\n");
            writer.write( "  Phone:    " +  (customer.getCustomerPhoneNo() != null ? customer.getCustomerPhoneNo() : "N/A") + "\n");
            writer.write( "----------------------------------------------------\n" );
            writer.write( "Tour Details:\n" );
            writer.write( "  ID:          " +  tour.getTourId() + "\n" );
            writer.write( "  Name:        " +  tour.getTourName() + "\n" );
            writer.write( "  Destination: " +  tour.getDestination() + "\n" );
            writer.write( "  Duration:    " +  tour.getDurationInDays() + " days\n");
            writer.write( "  Price:       " +  "$" + String.format("%.2f", tour.getPrice()) + "\n");
            writer.write( "  Sites:       " +  String.join(", ", tour.getAvailableSites()) + "\n" );
            writer.write( "----------------------------------------------------\n" );
            if (agent != null) {
                writer.write("Booked By Travel Agent:\n");
                writer.write("  Username: " + agent.getUsername() + "\n");
                writer.write( "  Name:     " +  (agent.getAgentName() != null ? agent.getAgentName() : "N/A") + "\n");
                writer.write( "----------------------------------------------------\n" );
            }
            writer.write("Booking Confirmed!\n" );
            writer.write("====================================================\n");
            System.out.println(ColorCodes.GREEN + "Ticket saved to: " + fileName + ColorCodes.RESET);
        }
    }
}
