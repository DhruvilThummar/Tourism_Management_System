package src.Tourism_Management_System.User_Info;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.UUID; // For generating adminId
import src.Tourism_Management_System.ColorCodes;
import src.Tourism_Management_System.Tour_Info.Tour_Info;
import src.Tourism_Management_System.DBMS.DBMS; // Import DBMS for usernameExists

public class Admin {
    private String adminId;
    private String username;
    private String name;
    private String phoneNo;
    // Password is not stored in object for security reasons, only for login method

    public Admin(String adminId, String username, String name, String phoneNo) {
        this.adminId = adminId;
        this.username = username;
        this.name = name;
        this.phoneNo = phoneNo;
    }

    public Admin(String username, String password) { // Constructor for sign-up/login (minimal fields)
        this.username = username;
        this.name = username; // Default name to username
        this.phoneNo = "N/A"; // Default phone to N/A
        this.adminId = UUID.randomUUID().toString(); // Generate ID for new admin if not from DB
    }

    public String getAdminId() { return adminId; }
    public String getUsername() { return username; }
    public String getName() { return name; }
    public String getPhoneNo() { return phoneNo; }

    public static Admin login(String username, String password, Connection con) {
        String query = "SELECT admin_id, admin_name, admin_phone_no, username FROM Admins WHERE username = ? AND password = ?";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, username);
            pst.setString(2, password);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    String id = rs.getString("admin_id");
                    String name = rs.getString("admin_name");
                    String phone = rs.getString("admin_phone_no");
                    String foundUsername = rs.getString("username");
                    return new Admin(id, foundUsername, name, phone);
                }
            }
        } catch (SQLException e) {
            System.err.println(ColorCodes.RED + "Error during Admin login: " + e.getMessage() + ColorCodes.RESET);
        }
        return null;
    }

    public static boolean signUp(String username, String password, Connection con) {
        if (DBMS.usernameExists(username, "Admins", con)) { // Using centralized check
            System.out.println(ColorCodes.YELLOW + "Username already exists. Please choose a different one." + ColorCodes.RESET);
            return false;
        }
        String adminId = UUID.randomUUID().toString(); // Generate a unique ID for the new admin
        String query = "INSERT INTO Admins (admin_id, username, password, admin_name, admin_phone_no) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, adminId);
            pst.setString(2, username);
            pst.setString(3, password);
            pst.setString(4, username); // Default name to username
            pst.setString(5, "N/A"); // Default phone to N/A
            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println(ColorCodes.RED + "Error during Admin sign-up: " + e.getMessage() + ColorCodes.RESET);
            return false;
        }
    }

    public void manageTours(Connection con, Scanner sc) {
        System.out.println(ColorCodes.WHITE + "\nAdmin " + this.username + " is managing tours..." + ColorCodes.RESET);
        boolean managingTours = true;
        while(managingTours) {
            System.out.println(ColorCodes.BOLD + ColorCodes.CYAN + "--- Tour Management ---" + ColorCodes.RESET);
            System.out.println(ColorCodes.YELLOW + "1. " + ColorCodes.WHITE + "Add New Tour" + ColorCodes.RESET);
            System.out.println(ColorCodes.YELLOW + "2. " + ColorCodes.WHITE + "View All Tours" + ColorCodes.RESET);
            System.out.println(ColorCodes.YELLOW + "3. " + ColorCodes.WHITE + "Update Tour Details" + ColorCodes.RESET);
            System.out.println(ColorCodes.YELLOW + "4. " + ColorCodes.WHITE + "Delete Tour" + ColorCodes.RESET);
            System.out.println(ColorCodes.YELLOW + "5. " + ColorCodes.RED + "Back to Admin Dashboard" + ColorCodes.RESET);
            System.out.print(ColorCodes.BOLD + ColorCodes.CYAN + "Enter your choice: " + ColorCodes.RESET);
            int choice = -1;
            try {
                choice = sc.nextInt();
            } catch (InputMismatchException e) {
                System.out.println(ColorCodes.RED + "Invalid input. Please enter a number." + ColorCodes.RESET);
                sc.next();
                continue;
            }
            sc.nextLine(); // Consume newline

            try {
                switch (choice) {
                    case 1 -> {
                        System.out.print(ColorCodes.CYAN + "Enter Tour ID: " + ColorCodes.RESET); String id = sc.nextLine();
                        System.out.print(ColorCodes.CYAN + "Enter Tour Name: " + ColorCodes.RESET); String name = sc.nextLine();
                        System.out.print(ColorCodes.CYAN + "Enter Destination: " + ColorCodes.RESET); String dest = sc.nextLine();

                        int duration = -1;
                        while(duration <= 0) {
                            try {
                                System.out.print(ColorCodes.CYAN + "Enter Duration (days): " + ColorCodes.RESET);
                                duration = sc.nextInt();
                                if (duration <= 0) System.out.println(ColorCodes.RED + "Duration must be a positive number." + ColorCodes.RESET);
                            } catch (InputMismatchException e) {
                                System.out.println(ColorCodes.RED + "Invalid input. Please enter a number for duration." + ColorCodes.RESET);
                                sc.next(); // Consume invalid input
                            } finally {
                                sc.nextLine(); // Consume newline
                            }
                        }

                        double price = -1;
                        while(price < 0) {
                            try {
                                System.out.print(ColorCodes.CYAN + "Enter Price: " + ColorCodes.RESET);
                                price = sc.nextDouble();
                                if (price < 0) System.out.println(ColorCodes.RED + "Price cannot be negative." + ColorCodes.RESET);
                            } catch (InputMismatchException e) {
                                System.out.println(ColorCodes.RED + "Invalid input. Please enter a number for price." + ColorCodes.RESET);
                                sc.next(); // Consume invalid input
                            } finally {
                                sc.nextLine(); // Consume newline
                            }
                        }

                        System.out.print(ColorCodes.CYAN + "Enter Description: " + ColorCodes.RESET); String desc = sc.nextLine();

                        int maxSize = -1;
                        while(maxSize <= 0) {
                            try {
                                System.out.print(ColorCodes.CYAN + "Enter Max Group Size: " + ColorCodes.RESET);
                                maxSize = sc.nextInt();
                                if (maxSize <= 0) System.out.println(ColorCodes.RED + "Max Group Size must be a positive number." + ColorCodes.RESET);
                            } catch (InputMismatchException e) {
                                System.out.println(ColorCodes.RED + "Invalid input. Please enter a number for max group size." + ColorCodes.RESET);
                                sc.next(); // Consume invalid input
                            } finally {
                                sc.nextLine(); // Consume newline
                            }
                        }

                        System.out.print(ColorCodes.CYAN + "Enter Available Sites (comma-separated): " + ColorCodes.RESET);
                        String sitesInput = sc.nextLine();
                        List<String> sites = List.of(sitesInput.split(",\\s*"));

                        Tour_Info newTour = new Tour_Info(id, name, dest, duration, price, desc, maxSize, sites);
                        newTour.save(con);
                        System.out.println(ColorCodes.GREEN + "Tour added successfully!" + ColorCodes.RESET);
                    }
                    case 2 -> {
                        List<Tour_Info> tours = Tour_Info.getAllTours(con);
                        if (tours.isEmpty()) {
                            System.out.println(ColorCodes.YELLOW + "No tours available." + ColorCodes.RESET);
                        } else {
                            System.out.println(ColorCodes.BOLD + ColorCodes.WHITE + "\n--- All Tours ---" + ColorCodes.RESET);
                            tours.forEach(System.out::println);
                            System.out.println(ColorCodes.BOLD + ColorCodes.WHITE + "-----------------\n" + ColorCodes.RESET);
                        }
                    }
                    case 3 -> {
                        System.out.print(ColorCodes.CYAN + "Enter Tour ID to update: " + ColorCodes.RESET);
                        String tourId = sc.nextLine();
                        Tour_Info tourToUpdate = Tour_Info.load(con, tourId);
                        if (tourToUpdate == null) {
                            System.out.println(ColorCodes.RED + "Tour not found." + ColorCodes.RESET);
                            break;
                        }
                        System.out.println(ColorCodes.BOLD + ColorCodes.WHITE + "\nCurrent Tour Details:" + ColorCodes.RESET);
                        System.out.println(tourToUpdate.toString());

                        System.out.print(ColorCodes.CYAN + "Enter New Tour Name (leave blank to keep current): " + ColorCodes.RESET);
                        String newName = sc.nextLine();
                        if (!newName.isEmpty()) tourToUpdate.setTourName(newName);

                        System.out.print(ColorCodes.CYAN + "Enter New Destination (leave blank to keep current): " + ColorCodes.RESET);
                        String newDest = sc.nextLine();
                        if (!newDest.isEmpty()) tourToUpdate.setDestination(newDest);

                        System.out.print(ColorCodes.CYAN + "Enter New Duration in Days (0 to keep current): " + ColorCodes.RESET);
                        String durationStr = sc.nextLine();
                        if (!durationStr.isEmpty()) {
                            try {
                                int newDuration = Integer.parseInt(durationStr);
                                if (newDuration > 0) tourToUpdate.setDurationInDays(newDuration);
                                else System.out.println(ColorCodes.YELLOW + "Duration must be positive. Keeping current value." + ColorCodes.RESET);
                            } catch (NumberFormatException e) {
                                System.out.println(ColorCodes.RED + "Invalid number format for duration. Keeping current value." + ColorCodes.RESET);
                            }
                        }

                        System.out.print(ColorCodes.CYAN + "Enter New Price (0 to keep current): " + ColorCodes.RESET);
                        String priceStr = sc.nextLine();
                        if (!priceStr.isEmpty()) {
                            try {
                                double newPrice = Double.parseDouble(priceStr);
                                if (newPrice >= 0) tourToUpdate.setPrice(newPrice);
                                else System.out.println(ColorCodes.YELLOW + "Price cannot be negative. Keeping current value." + ColorCodes.RESET);
                            } catch (NumberFormatException e) {
                                System.out.println(ColorCodes.RED + "Invalid number format for price. Keeping current value." + ColorCodes.RESET);
                            }
                        }

                        System.out.print(ColorCodes.CYAN + "Enter New Description (leave blank to keep current): " + ColorCodes.RESET);
                        String newDesc = sc.nextLine();
                        if (!newDesc.isEmpty()) tourToUpdate.setDescription(newDesc);

                        System.out.print(ColorCodes.CYAN + "Enter New Max Group Size (0 to keep current): " + ColorCodes.RESET);
                        String maxSizeStr = sc.nextLine();
                        if (!maxSizeStr.isEmpty()) {
                            try {
                                int newMax = Integer.parseInt(maxSizeStr);
                                if (newMax > 0) {
                                    // Adjust available slots if max group size changes
                                    int currentBookedSlots = tourToUpdate.getMaxGroupSize() - tourToUpdate.getAvailableSlots();
                                    tourToUpdate.setMaxGroupSize(newMax);
                                    tourToUpdate.setAvailableSlots(newMax - currentBookedSlots); // Recalculate based on booked
                                    if (tourToUpdate.getAvailableSlots() < 0) {
                                        tourToUpdate.setAvailableSlots(0); // Cannot be negative
                                        System.out.println(ColorCodes.YELLOW + "Warning: New max group size is less than current bookings. Available slots set to 0." + ColorCodes.RESET);
                                    }
                                } else System.out.println(ColorCodes.YELLOW + "Max Group Size must be positive. Keeping current value." + ColorCodes.RESET);
                            } catch (NumberFormatException e) {
                                System.out.println(ColorCodes.RED + "Invalid number format for max group size. Keeping current value." + ColorCodes.RESET);
                            }
                        }

                        System.out.print(ColorCodes.CYAN + "Enter New Available Sites (comma-separated, leave blank to keep current): " + ColorCodes.RESET);
                        String newSitesInput = sc.nextLine();
                        if (!newSitesInput.isEmpty()) {
                            tourToUpdate.setAvailableSites(List.of(newSitesInput.split(",\\s*")));
                        }

                        tourToUpdate.update(con);
                        System.out.println(ColorCodes.GREEN + "Tour updated successfully!" + ColorCodes.RESET);
                    }
                    case 4 -> {
                        System.out.print(ColorCodes.CYAN + "Enter Tour ID to delete: " + ColorCodes.RESET);
                        String tourId = sc.nextLine();
                        Tour_Info tourToDelete = Tour_Info.load(con, tourId);
                        if (tourToDelete == null) {
                            System.out.println(ColorCodes.RED + "Tour not found." + ColorCodes.RESET);
                            break;
                        }
                        System.out.print(ColorCodes.YELLOW + "Are you sure you want to delete tour '" + tourToDelete.getTourName() + "' (Y/N)? " + ColorCodes.RESET);
                        String confirm = sc.nextLine();
                        if (confirm.equalsIgnoreCase("Y")) {
                            tourToDelete.delete(con);
                            System.out.println(ColorCodes.GREEN + "Tour deleted successfully!" + ColorCodes.RESET);
                        } else {
                            System.out.println(ColorCodes.WHITE + "Tour deletion cancelled." + ColorCodes.RESET);
                        }
                    }
                    case 5 -> managingTours = false;
                    default -> System.out.println(ColorCodes.RED + "Invalid choice. Please try again." + ColorCodes.RESET);
                }
            } catch (SQLException e) {
                System.err.println(ColorCodes.RED + "Database error during tour management: " + e.getMessage() + ColorCodes.RESET);
            }
            System.out.println();
        }
    }

    public void manageUsers(Connection con, Scanner sc) {
        System.out.println(ColorCodes.WHITE + "\nAdmin " + this.username + " is managing users..." + ColorCodes.RESET);
        boolean managingUsers = true;
        while(managingUsers) {
            System.out.println(ColorCodes.BOLD + ColorCodes.CYAN + "--- User Management ---" + ColorCodes.RESET);
            System.out.println(ColorCodes.YELLOW + "1. " + ColorCodes.WHITE + "View All Admins" + ColorCodes.RESET);
            System.out.println(ColorCodes.YELLOW + "2. " + ColorCodes.WHITE + "View All Travel Agents" + ColorCodes.RESET);
            System.out.println(ColorCodes.YELLOW + "3. " + ColorCodes.WHITE + "View All Customers" + ColorCodes.RESET);
            System.out.println(ColorCodes.YELLOW + "4. " + ColorCodes.RED + "Back to Admin Dashboard" + ColorCodes.RESET);
            System.out.print(ColorCodes.BOLD + ColorCodes.CYAN + "Enter your choice: " + ColorCodes.RESET);
            int choice = -1;
            try {
                choice = sc.nextInt();
            } catch (InputMismatchException e) {
                System.out.println(ColorCodes.RED + "Invalid input. Please enter a number." + ColorCodes.RESET);
                sc.next();
                continue;
            }
            sc.nextLine();

            try {
                switch (choice) {
                    case 1 -> viewAllUsers("Admins", con);
                    case 2 -> viewAllUsers("TravelAgents", con);
                    case 3 -> viewAllUsers("Customers", con);
                    case 4 -> managingUsers = false;
                    default -> System.out.println(ColorCodes.RED + "Invalid choice. Please try again." + ColorCodes.RESET);
                }
            } catch (SQLException e) {
                System.err.println(ColorCodes.RED + "Database error during user management: " + e.getMessage() + ColorCodes.RESET);
            }
            System.out.println();
        }
    }

    private void viewAllUsers(String tableName, Connection con) throws SQLException {
        String query = "SELECT * FROM " + tableName;
        System.out.println(ColorCodes.BOLD + ColorCodes.WHITE + "\n--- Listing all " + tableName + " ---" + ColorCodes.RESET);
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (!rs.isBeforeFirst()) {
                System.out.println(ColorCodes.YELLOW + "No " + tableName + " found." + ColorCodes.RESET);
                return;
            }
            // Print column headers
            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                System.out.printf(ColorCodes.BOLD + "%-25s" + ColorCodes.RESET, rs.getMetaData().getColumnName(i));
            }
            System.out.println();
            System.out.println("----------------------------------------------------------------------------------------------------"); // Separator

            while (rs.next()) {
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    System.out.printf("%-25s", rs.getString(i));
                }
                System.out.println();
            }
            System.out.println("----------------------------------------------------------------------------------------------------"); // Separator
        }
    }

    public void viewReports(Connection con) {
        System.out.println(ColorCodes.WHITE + "\nAdmin " + this.username + " is viewing system reports..." + ColorCodes.RESET);
        System.out.println(ColorCodes.BOLD + ColorCodes.CYAN + "--- System Reports ---" + ColorCodes.RESET);

        try {
            String queryTours = "SELECT COUNT(*) AS total_tours FROM Tour";
            try (Statement stmt = con.createStatement();
                 ResultSet rs = stmt.executeQuery(queryTours)) {
                if (rs.next()) {
                    System.out.println(ColorCodes.YELLOW + "Total Tours Available: " + ColorCodes.WHITE + rs.getInt("total_tours") + ColorCodes.RESET);
                }
            }

            String queryBookings = "SELECT COUNT(*) AS total_bookings FROM Bookings";
            try (Statement stmt = con.createStatement();
                 ResultSet rs = stmt.executeQuery(queryBookings)) {
                if (rs.next()) {
                    System.out.println(ColorCodes.YELLOW + "Total Bookings Made: " + ColorCodes.WHITE + rs.getInt("total_bookings") + ColorCodes.RESET);
                }
            }

            String queryAvailableTours = "SELECT tour_name, available_slots FROM Tour WHERE available_slots > 0 ORDER BY available_slots DESC";
            System.out.println(ColorCodes.BOLD + ColorCodes.WHITE + "\n--- Tours with Available Slots ---" + ColorCodes.RESET);
            try (Statement stmt = con.createStatement();
                 ResultSet rs = stmt.executeQuery(queryAvailableTours)) {
                if (!rs.isBeforeFirst()) {
                    System.out.println(ColorCodes.YELLOW + "No tours with available slots." + ColorCodes.RESET);
                } else {
                    while (rs.next()) {
                        System.out.println(ColorCodes.YELLOW + "  Tour: " + ColorCodes.WHITE + rs.getString("tour_name") + ", Slots: " + rs.getInt("available_slots") + ColorCodes.RESET);
                    }
                }
            }

            String queryPopularDestinations = "SELECT t.destination, COUNT(b.booking_id) AS total_bookings_to_dest " +
                    "FROM Bookings b JOIN Tour t ON b.tour_id = t.tour_id " +
                    "GROUP BY t.destination ORDER BY total_bookings_to_dest DESC LIMIT 5";
            System.out.println(ColorCodes.BOLD + ColorCodes.WHITE + "\n--- Top 5 Popular Destinations (by Bookings) ---" + ColorCodes.RESET);
            try (Statement stmt = con.createStatement();
                 ResultSet rs = stmt.executeQuery(queryPopularDestinations)) {
                if (!rs.isBeforeFirst()) {
                    System.out.println(ColorCodes.YELLOW + "No bookings yet to determine popular destinations." + ColorCodes.RESET);
                } else {
                    while (rs.next()) {
                        System.out.println(ColorCodes.YELLOW + "  Destination: " + ColorCodes.WHITE + rs.getString("destination") + ", Bookings: " + rs.getInt("total_bookings_to_dest") + ColorCodes.RESET);
                    }
                }
            }
            System.out.println(ColorCodes.BOLD + ColorCodes.WHITE + "--------------------------------------------------\n" + ColorCodes.RESET);


        } catch (SQLException e) {
            System.err.println(ColorCodes.RED + "Error generating reports: " + e.getMessage() + ColorCodes.RESET);
        }
    }

    public void manageAdminProfile(Connection con, Scanner sc) {
        System.out.println(ColorCodes.WHITE + "\nAdmin " + this.username + " is managing their profile..." + ColorCodes.RESET);
        System.out.println(ColorCodes.BOLD + ColorCodes.CYAN + "--- Your Current Profile ---" + ColorCodes.RESET);
        System.out.println(ColorCodes.YELLOW + "Admin ID: " + ColorCodes.WHITE + this.adminId + ColorCodes.RESET);
        System.out.println(ColorCodes.YELLOW + "Username: " + ColorCodes.WHITE + this.username + ColorCodes.RESET);
        System.out.println(ColorCodes.YELLOW + "Name: " + ColorCodes.WHITE + (this.name != null && !this.name.isEmpty() ? this.name : "N/A") + ColorCodes.RESET);
        System.out.println(ColorCodes.YELLOW + "Phone No: " + ColorCodes.WHITE + (this.phoneNo != null && !this.phoneNo.isEmpty() ? this.phoneNo : "N/A") + ColorCodes.RESET);

        System.out.println(ColorCodes.WHITE + "\nEnter new details (leave blank to keep current):" + ColorCodes.RESET);
        System.out.print(ColorCodes.CYAN + "New Name: " + ColorCodes.RESET);
        String newName = sc.nextLine();
        System.out.print(ColorCodes.CYAN + "New Phone Number: " + ColorCodes.RESET);
        String newPhone = sc.nextLine();

        String updateSql = "UPDATE Admins SET admin_name = COALESCE(?, admin_name), admin_phone_no = COALESCE(?, admin_phone_no) WHERE admin_id = ?";
        try (PreparedStatement pst = con.prepareStatement(updateSql)) {
            pst.setString(1, newName.isEmpty() ? null : newName);
            pst.setString(2, newPhone.isEmpty() ? null : newPhone);
            pst.setString(3, this.adminId);

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                if (!newName.isEmpty()) this.name = newName;
                if (!newPhone.isEmpty()) this.phoneNo = newPhone;
                System.out.println(ColorCodes.GREEN + "Profile updated successfully!" + ColorCodes.RESET);
            } else {
                System.out.println(ColorCodes.RED + "Profile update failed." + ColorCodes.RESET);
            }
        } catch (SQLException e) {
            System.err.println(ColorCodes.RED + "Error updating admin profile: " + e.getMessage() + ColorCodes.RESET);
        }
    }
}