package src.Tourism_Management_System.User_Info;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import java.util.UUID; // For generating agentId
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import src.Tourism_Management_System.ColorCodes;
import src.Tourism_Management_System.Tour_Info.Tour_Info;
import src.Tourism_Management_System.Tasks.BookingTask; // Import the new task
import src.Tourism_Management_System.DBMS.DBMS; // Import DBMS for usernameExists

public class TravelAgent {
    private String agentId;
    private String agentName;
    private String agentPhoneNo;
    private String username;
    // private String password; // Password not stored in object for security

    // Add an ExecutorService for managing booking threads
    private static final ExecutorService agentBookingExecutor = Executors.newCachedThreadPool();

    public TravelAgent(String agentId, String agentName, String agentPhoneNo, String username) {
        this.agentId = agentId;
        this.agentName = agentName;
        this.agentPhoneNo = agentPhoneNo;
        this.username = username;
    }

    public TravelAgent(String username, String password) { // Constructor for sign-up/login
        this.username = username;
        this.agentName = username; // Default name to username
        this.agentPhoneNo = "N/A"; // Default phone to N/A
        this.agentId = UUID.randomUUID().toString(); // Generate ID for new agent
    }

    public String getAgentId() { return agentId; }
    public String getAgentName() { return agentName; }
    public String getAgentPhoneNo() { return agentPhoneNo; }
    public String getUsername() { return username; }

    public static TravelAgent login(String username, String password, Connection con) {
        String query = "SELECT agent_id, agent_name, agent_phone_no, username FROM TravelAgents WHERE username = ? AND password = ?";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, username);
            pst.setString(2, password);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    String id = rs.getString("agent_id");
                    String name = rs.getString("agent_name");
                    String phone = rs.getString("agent_phone_no");
                    String foundUsername = rs.getString("username");
                    return new TravelAgent(id, name, phone, foundUsername);
                }
            }
        } catch (SQLException e) {
            System.err.println(ColorCodes.RED + "Error during Travel Agent login: " + e.getMessage() + ColorCodes.RESET);
        }
        return null;
    }

    public static boolean signUp(String username, String password, Connection con) {
        if (DBMS.usernameExists(username, "TravelAgents", con)) { // Using centralized check
            System.out.println(ColorCodes.YELLOW + "Username already exists. Please choose a different one." + ColorCodes.RESET);
            return false;
        }
        String agentId = UUID.randomUUID().toString(); // Generate a unique ID for the new agent
        String query = "INSERT INTO TravelAgents (agent_id, username, password, agent_name, agent_phone_no) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, agentId);
            pst.setString(2, username);
            pst.setString(3, password);
            pst.setString(4, username); // Default name to username
            pst.setString(5, "N/A"); // Default phone to N/A
            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println(ColorCodes.RED + "Error during Travel Agent sign-up: " + e.getMessage() + ColorCodes.RESET);
            return false;
        }
    }

    public void viewAvailableTours(Connection con) {
        System.out.println(ColorCodes.WHITE + "\nAgent " + this.username + " is viewing available tours..." + ColorCodes.RESET);
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
            System.err.println(ColorCodes.RED + "Error viewing tours: " + e.getMessage() + ColorCodes.RESET);
        }
    }

    public void bookTourForCustomer(Connection con, Scanner sc) {
        System.out.println(ColorCodes.WHITE + "\nAgent " + this.username + " is booking a tour for a customer..." + ColorCodes.RESET);
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

            System.out.print(ColorCodes.CYAN + "Enter Customer Username (existing or new to create): " + ColorCodes.RESET);
            String customerUsername = sc.nextLine();
            Customer customer = Customer.login(customerUsername, "dummy_password", con); // Login with dummy password for initial check

            if (customer == null) {
                System.out.println(ColorCodes.YELLOW + "Customer not found. Let's create a new customer account." + ColorCodes.RESET);
                System.out.print(ColorCodes.CYAN + "Enter New Customer Password: " + ColorCodes.RESET);
                String newCustPassword = sc.nextLine();
                System.out.print(ColorCodes.CYAN + "Enter New Customer Email: " + ColorCodes.RESET);
                String newCustEmail = sc.nextLine();
                System.out.print(ColorCodes.CYAN + "Enter New Customer Phone: " + ColorCodes.RESET);
                String newCustPhone = sc.nextLine();

                if (Customer.signUp(customerUsername, newCustPassword, newCustEmail, newCustPhone, con)) {
                    System.out.println(ColorCodes.GREEN + "New customer account created successfully! Attempting to log in for booking..." + ColorCodes.RESET);
                    customer = Customer.login(customerUsername, newCustPassword, con); // Re-login to get full customer object
                    if (customer == null) {
                        System.out.println(ColorCodes.RED + "Failed to log in newly created customer. Booking aborted." + ColorCodes.RESET);
                        return;
                    }
                } else {
                    System.out.println(ColorCodes.RED + "Failed to create new customer account. Booking aborted." + ColorCodes.RESET);
                    return;
                }
            }
            // Submit the booking task to the executor service
            agentBookingExecutor.submit(new BookingTask(tour, customer, this, con, "agent"));
            System.out.println(ColorCodes.CYAN + "Booking request submitted by agent. Please wait for confirmation..." + ColorCodes.RESET);

        } catch (SQLException e) {
            System.err.println(ColorCodes.RED + "Error preparing tour booking: " + e.getMessage() + ColorCodes.RESET);
        }
    }


    public void viewMyBookings(Connection con) {
        System.out.println(ColorCodes.WHITE + "\nAgent " + this.username + " is viewing bookings they made..." + ColorCodes.RESET);
        String query = "SELECT b.booking_id, t.tour_name, c.username AS customer_username, b.booking_date, b.status " +
                "FROM Bookings b " +
                "JOIN Tour t ON b.tour_id = t.tour_id " +
                "JOIN Customers c ON b.customer_id = c.customer_id " +
                "WHERE b.agent_id = ?";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, this.agentId);
            try (ResultSet rs = pst.executeQuery()) {
                if (!rs.isBeforeFirst()) {
                    System.out.println(ColorCodes.YELLOW + "You haven't made any bookings yet." + ColorCodes.RESET);
                } else {
                    System.out.println(ColorCodes.BOLD + ColorCodes.CYAN + "--- Your Bookings ---" + ColorCodes.RESET);
                    System.out.printf(ColorCodes.BOLD + "%-15s | %-30s | %-20s | %-12s | %-15s\n" + ColorCodes.RESET, "Booking ID", "Tour Name", "Customer Username", "Date", "Status");
                    System.out.println("--------------------------------------------------------------------------------------------------");
                    while (rs.next()) {
                        System.out.printf("%-15s | %-30s | %-20s | %-12s | %-15s\n",
                                rs.getString("booking_id"),
                                rs.getString("tour_name"),
                                rs.getString("customer_username"),
                                rs.getDate("booking_date"),
                                rs.getString("status"));
                    }
                    System.out.println("--------------------------------------------------------------------------------------------------");
                }
            }
        } catch (SQLException e) {
            System.err.println(ColorCodes.RED + "Error viewing agent bookings: " + e.getMessage() + ColorCodes.RESET);
        }
    }

    public void manageAgentProfile(Connection con, Scanner sc) {
        System.out.println(ColorCodes.WHITE + "\nAgent " + this.username + " is managing their profile..." + ColorCodes.RESET);
        System.out.println(ColorCodes.BOLD + ColorCodes.CYAN + "--- Your Current Profile ---" + ColorCodes.RESET);
        System.out.println(ColorCodes.YELLOW + "Agent ID: " + ColorCodes.WHITE + this.agentId + ColorCodes.RESET);
        System.out.println(ColorCodes.YELLOW + "Username: " + ColorCodes.WHITE + this.username + ColorCodes.RESET);
        System.out.println(ColorCodes.YELLOW + "Name: " + ColorCodes.WHITE + (this.agentName != null && !this.agentName.isEmpty() ? this.agentName : "N/A") + ColorCodes.RESET);
        System.out.println(ColorCodes.YELLOW + "Phone No: " + ColorCodes.WHITE + (this.agentPhoneNo != null && !this.agentPhoneNo.isEmpty() ? this.agentPhoneNo : "N/A") + ColorCodes.RESET);

        System.out.println(ColorCodes.WHITE + "\nEnter new details (leave blank to keep current):" + ColorCodes.RESET);
        System.out.print(ColorCodes.CYAN + "New Name: " + ColorCodes.RESET);
        String newName = sc.nextLine();
        System.out.print(ColorCodes.CYAN + "New Phone Number: " + ColorCodes.RESET);
        String newPhone = sc.nextLine();

        String updateSql = "UPDATE TravelAgents SET agent_name = COALESCE(?, agent_name), agent_phone_no = COALESCE(?, agent_phone_no) WHERE agent_id = ?";
        try (PreparedStatement pst = con.prepareStatement(updateSql)) {
            pst.setString(1, newName.isEmpty() ? null : newName);
            pst.setString(2, newPhone.isEmpty() ? null : newPhone);
            pst.setString(3, this.agentId);

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                if (!newName.isEmpty()) this.agentName = newName;
                if (!newPhone.isEmpty()) this.agentPhoneNo = newPhone;
                System.out.println(ColorCodes.GREEN + "Profile updated successfully!" + ColorCodes.RESET);
            } else {
                System.out.println(ColorCodes.RED + "Profile update failed." + ColorCodes.RESET);
            }
        } catch (SQLException e) {
            System.err.println(ColorCodes.RED + "Error updating agent profile: " + e.getMessage() + ColorCodes.RESET);
        }
    }

    // Add a shutdown hook for the ExecutorService when the application exits
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            agentBookingExecutor.shutdown();
            System.out.println(ColorCodes.WHITE + "Agent booking executor service shut down." + ColorCodes.RESET);
        }));
    }
}