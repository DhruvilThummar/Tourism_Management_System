package src.Tourism_Management_System.User_Info;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import java.util.UUID; // For generating customerId
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import src.Tourism_Management_System.ColorCodes;
import src.Tourism_Management_System.Tour_Info.Tour_Info;
import src.Tourism_Management_System.Tasks.BookingTask; // Import the new task
import src.Tourism_Management_System.DBMS.DBMS; // Import DBMS for usernameExists

public class Customer {
    private String customerId;
    private String username;
    private String customerName;
    private String customerPhoneNo;
    private String email;


    private static final ExecutorService bookingExecutor = Executors.newCachedThreadPool();

    public Customer(String customerId, String username, String customerName, String customerPhoneNo, String email) {
        this.customerId = customerId;
        this.username = username;
        this.customerName = customerName;
        this.customerPhoneNo = customerPhoneNo;
        this.email = email;
    }

    public Customer(String username, String password, String email, String customerPhoneNo) { // Constructor for sign-up
        this.username = username;
        this.customerName = username; // Default name to username
        this.customerPhoneNo = customerPhoneNo;
        this.email = email;
        this.customerId = UUID.randomUUID().toString(); // Generate unique ID for new customer
    }

    public String getCustomerId() { return customerId; }
    public String getUsername() { return username; }
    public String getCustomerName() { return customerName; }
    public String getCustomerPhoneNo() { return customerPhoneNo; }
    public String getEmail() { return email; }

    public static Customer login(String username, String password, Connection con) {
        String query = "SELECT customer_id, username, customer_name, customer_phone_no, email FROM Customers WHERE username = ? AND password = ?";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, username);
            pst.setString(2, password);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    String id = rs.getString("customer_id");
                    String name = rs.getString("customer_name");
                    String phone = rs.getString("customer_phone_no");
                    String email = rs.getString("email");
                    String foundUsername = rs.getString("username");
                    return new Customer(id, foundUsername, name, phone, email);
                }
            }
        } catch (SQLException e) {
            System.err.println(ColorCodes.RED + "Error during Customer login: " + e.getMessage() + ColorCodes.RESET);
        }
        return null;
    }

    public static boolean signUp(String username, String password, String email, String phone, Connection con) {
        if (DBMS.usernameExists(username, "Customers", con)) { // Using centralized check
            System.out.println(ColorCodes.YELLOW + "Username already exists. Please choose a different one." + ColorCodes.RESET);
            return false;
        }
        String customerId = UUID.randomUUID().toString(); // Generate a unique ID for the new customer
        String query = "INSERT INTO Customers (customer_id, username, password, customer_name, customer_phone_no, email) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, customerId);
            pst.setString(2, username);
            pst.setString(3, password);
            pst.setString(4, username); // Default name to username
            pst.setString(5, phone);
            pst.setString(6, email);
            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println(ColorCodes.RED + "Error during Customer sign-up: " + e.getMessage() + ColorCodes.RESET);
            return false;
        }
    }

    public void browseTours(Connection con) {
        System.out.println(ColorCodes.WHITE + "\nCustomer " + this.username + " is Browse tours..." + ColorCodes.RESET);
        try {
            List<Tour_Info> tours = Tour_Info.getAllTours(con);
            if (tours.isEmpty()) {
                System.out.println(ColorCodes.YELLOW + "No tours available at the moment." + ColorCodes.RESET);
            } else {
                System.out.println(ColorCodes.BOLD + ColorCodes.CYAN + "--- All Available Tours ---" + ColorCodes.RESET);
                for (Tour_Info tour : tours) {
                    System.out.println(tour.toString());
                    System.out.println("----------------------------------------------------"); // Separator for tours
                }
                System.out.println(ColorCodes.BOLD + ColorCodes.CYAN + "---------------------------\n" + ColorCodes.RESET);
            }
        } catch (SQLException e) {
            System.err.println(ColorCodes.RED + "Error Browse tours: " + e.getMessage() + ColorCodes.RESET);
        }
    }

    public void bookTour(Connection con, Scanner sc) {
        System.out.println(ColorCodes.WHITE + "\nCustomer " + this.username + " is booking a tour..." + ColorCodes.RESET);
        System.out.print(ColorCodes.CYAN + "Enter Tour ID to book: " + ColorCodes.RESET);
        String tourId = sc.nextLine();

        try {
            Tour_Info tour = Tour_Info.load(con, tourId);
            if (tour == null) {
                System.out.println(ColorCodes.RED + "Tour with ID " + tourId + " not found." + ColorCodes.RESET);
                return;
            }

            if (tour.getAvailableSlots() <= 0) {
                System.out.println(ColorCodes.YELLOW + "No available slots for this tour." + ColorCodes.RESET);
                return;
            }

            // Submit the booking task to the executor service
            bookingExecutor.submit(new BookingTask(tour, this, con, "customer"));
            System.out.println(ColorCodes.CYAN + "Booking request submitted. Please wait for confirmation..." + ColorCodes.RESET);

        } catch (SQLException e) {
            System.err.println(ColorCodes.RED + "Error preparing tour booking: " + e.getMessage() + ColorCodes.RESET);
        }
    }

    public void viewMyBookings(Connection con) {
        System.out.println(ColorCodes.WHITE + "\nCustomer " + this.username + " is viewing their bookings..." + ColorCodes.RESET);
        String query = "SELECT b.booking_id, t.tour_name, b.booking_date, b.status " +
                "FROM Bookings b " +
                "JOIN Tour t ON b.tour_id = t.tour_id " +
                "WHERE b.customer_id = ?";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, this.customerId);
            try (ResultSet rs = pst.executeQuery()) {
                if (!rs.isBeforeFirst()) {
                    System.out.println(ColorCodes.YELLOW + "You haven't made any bookings yet." + ColorCodes.RESET);
                } else {
                    System.out.println(ColorCodes.BOLD + ColorCodes.CYAN + "--- Your Bookings ---" + ColorCodes.RESET);
                    System.out.printf(ColorCodes.BOLD + "%-15s | %-30s | %-12s | %-15s\n" + ColorCodes.RESET, "Booking ID", "Tour Name", "Date", "Status");
                    System.out.println("-----------------------------------------------------------------------------");
                    while (rs.next()) {
                        System.out.printf("%-15s | %-30s | %-12s | %-15s\n",
                                rs.getString("booking_id"),
                                rs.getString("tour_name"),
                                rs.getDate("booking_date"),
                                rs.getString("status"));
                    }
                    System.out.println("-----------------------------------------------------------------------------");
                }
            }
        } catch (SQLException e) {
            System.err.println(ColorCodes.RED + "Error viewing customer bookings: " + e.getMessage() + ColorCodes.RESET);
        }
    }

    public void cancelBooking(Connection con, Scanner sc) {
        System.out.print("Enter the Booking ID to cancel: ");
        int bookingId = sc.nextInt();
        sc.nextLine(); // Consume newline

        String findBookingSql = "SELECT tour_id, customer_id FROM Bookings WHERE booking_id = ? AND customer_id = ?";
        String tourId = null;

        try {
            // Start a transaction
            con.setAutoCommit(false);

            // 1. Find the booking and ensure it belongs to this customer
            PreparedStatement findPst = con.prepareStatement(findBookingSql);
            findPst.setInt(1, bookingId);
            findPst.setString(2, this.customerId);
            ResultSet rs = findPst.executeQuery();

            if (rs.next()) {
                tourId = rs.getString("tour_id");
            } else {
                System.out.println(ColorCodes.RED + "Error: Booking not found or you don't have permission to cancel it."+ ColorCodes.RESET);
                con.rollback(); // Abort transaction
                return;
            }

            // 2. Increment available slots in the tour table
            String updateSlotsSql = "UPDATE tour SET available_slots = available_slots + 1 WHERE tour_id = ?";
            PreparedStatement updateSlotsPst = con.prepareStatement(updateSlotsSql);
            updateSlotsPst.setString(1, tourId);
            updateSlotsPst.executeUpdate();

            // 3. Log the cancellation in the new 'cancellations' table
            String logCancellationSql = "INSERT INTO cancellations (booking_id, tour_id, customer_id) VALUES (?, ?, ?)";
            PreparedStatement logPst = con.prepareStatement(logCancellationSql);
            logPst.setInt(1, bookingId);
            logPst.setString(2, tourId);
            logPst.setString(3, this.customerId);
            logPst.executeUpdate();

            // 4. Delete the original booking from the 'bookings' table
            String deleteBookingSql = "DELETE FROM Bookings WHERE booking_id = ?";
            PreparedStatement deletePst = con.prepareStatement(deleteBookingSql);
            deletePst.setInt(1, bookingId);
            int rowsAffected = deletePst.executeUpdate();

            if (rowsAffected > 0) {
                con.commit(); // Finalize the transaction
                System.out.println(ColorCodes.YELLOW +"Booking " +ColorCodes.WHITE +  bookingId + ColorCodes.YELLOW +" has been successfully canceled."+ ColorCodes.RESET);
            } else {
                throw new SQLException(ColorCodes.RED +"Failed to delete the booking."+ ColorCodes.RESET);
            }

        } catch (SQLException e) {
            System.err.println("Cancellation failed. Rolling back changes. Error: " + e.getMessage());
            try {
                if (con != null) con.rollback(); // Rollback on error
            } catch (SQLException ex) {
                System.err.println("Error during rollback: " + ex.getMessage());
            }
        } finally {
            try {
                if (con != null) con.setAutoCommit(true); // Reset auto-commit
            } catch (SQLException e) {
                System.err.println("Error resetting auto-commit: " + e.getMessage());
            }
        }
    }

    public void updateProfile(Connection con, Scanner sc) {
        System.out.println(ColorCodes.WHITE + "\nCustomer " + this.username + " is updating their profile..." + ColorCodes.RESET);
        System.out.println(ColorCodes.BOLD + ColorCodes.CYAN + "--- Your Current Profile ---" + ColorCodes.RESET);
        System.out.println(ColorCodes.YELLOW + "Customer ID: " + ColorCodes.WHITE + this.customerId + ColorCodes.RESET);
        System.out.println(ColorCodes.YELLOW + "Username: " + ColorCodes.WHITE + this.username + ColorCodes.RESET);
        System.out.println(ColorCodes.YELLOW + "Name: " + ColorCodes.WHITE + (this.customerName != null && !this.customerName.isEmpty() ? this.customerName : "N/A") + ColorCodes.RESET);
        System.out.println(ColorCodes.YELLOW + "Email: " + ColorCodes.WHITE + (this.email != null && !this.email.isEmpty() ? this.email : "N/A") + ColorCodes.RESET);
        System.out.println(ColorCodes.YELLOW + "Phone No: " + ColorCodes.WHITE + (this.customerPhoneNo != null && !this.customerPhoneNo.isEmpty() ? this.customerPhoneNo : "N/A") + ColorCodes.RESET);

        System.out.println(ColorCodes.WHITE + "\nEnter new details (leave blank to keep current):" + ColorCodes.RESET);
        System.out.print(ColorCodes.CYAN + "New Name: " + ColorCodes.RESET);
        String newName = sc.nextLine();
        System.out.print(ColorCodes.CYAN + "New Email: " + ColorCodes.RESET);
        String newEmail = sc.nextLine();
        System.out.print(ColorCodes.CYAN + "New Phone Number: " + ColorCodes.RESET);
        String newPhone = sc.nextLine();

        String updateSql = "UPDATE Customers SET customer_name = COALESCE(?, customer_name), email = COALESCE(?, email), customer_phone_no = COALESCE(?, customer_phone_no) WHERE customer_id = ?";
        try (PreparedStatement pst = con.prepareStatement(updateSql)) {
            pst.setString(1, newName.isEmpty() ? null : newName);
            pst.setString(2, newEmail.isEmpty() ? null : newEmail);
            pst.setString(3, newPhone.isEmpty() ? null : newPhone);
            pst.setString(4, this.customerId);

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                if (!newName.isEmpty()) this.customerName = newName;
                if (!newEmail.isEmpty()) this.email = newEmail;
                if (!newPhone.isEmpty()) this.customerPhoneNo = newPhone;
                System.out.println(ColorCodes.GREEN + "Profile updated successfully!" + ColorCodes.RESET);
            } else {
                System.out.println(ColorCodes.RED + "Profile update failed." + ColorCodes.RESET);
            }
        } catch (SQLException e) {
            System.err.println(ColorCodes.RED + "Error updating customer profile: " + e.getMessage() + ColorCodes.RESET);
        }
    }

    // Add a shutdown hook for the ExecutorService when the application exits
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            bookingExecutor.shutdown();
            System.out.println(ColorCodes.WHITE + "Customer booking executor service shut down." + ColorCodes.RESET);
        }));
    }
}
