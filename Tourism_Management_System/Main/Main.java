package src.Tourism_Management_System.Main;

import src.Tourism_Management_System.ColorCodes;
import src.Tourism_Management_System.DBMS.DBMS;
import src.Tourism_Management_System.User_Info.Admin;
import src.Tourism_Management_System.User_Info.Customer;
import src.Tourism_Management_System.User_Info.TravelAgent;

import java.sql.Connection;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    static Scanner sc = new Scanner(System.in);
    static Connection con;

    public static void main(String[] args) {
        DBMS.connect();
        con = DBMS.con;

        System.out.println(ColorCodes.BOLD + ColorCodes.CYAN + "====== Welcome to the Travel Management System ======" + ColorCodes.RESET);
        System.out.println();
        boolean running = true;
        while(running) {
            System.out.println(ColorCodes.BOLD + ColorCodes.WHITE + "--- Main Menu ---" + ColorCodes.RESET);
            System.out.println(ColorCodes.YELLOW + "Press 1 " + ColorCodes.RESET + "→ " + ColorCodes.GREEN + "Admin Login" + ColorCodes.RESET);
            System.out.println(ColorCodes.YELLOW + "Press 2 " + ColorCodes.RESET + "→ " + ColorCodes.PURPLE + "Travel Agent Login" + ColorCodes.RESET);
            System.out.println(ColorCodes.YELLOW + "Press 3 " + ColorCodes.RESET + "→ " + ColorCodes.BLUE + "Customer Login" + ColorCodes.RESET);
            System.out.println(ColorCodes.YELLOW + "Press 4 " + ColorCodes.RESET + "→ " + ColorCodes.RED + "Exit" + ColorCodes.RESET);
            System.out.println();
            System.out.print(ColorCodes.BOLD + ColorCodes.CYAN + "Enter your choice: " + ColorCodes.RESET);
            int choice = -1;
            try {
                choice = sc.nextInt();
            } catch (InputMismatchException e) {
                System.out.println(ColorCodes.RED + "Invalid input. Please enter a number." + ColorCodes.RESET);
                sc.next(); // Consume the invalid input
                continue;
            }
            sc.nextLine(); // Consume the rest of the line

            Main app = new Main();

            switch (choice) {
                case 1 -> {
                    Admin loggedInAdmin = app.processAdminLogin();
                    if (loggedInAdmin != null) {
                        System.out.println(ColorCodes.GREEN + "Admin login successful! Welcome, " + loggedInAdmin.getUsername() + "!" + ColorCodes.RESET);
                        app.showAdminMenu(loggedInAdmin);
                    } else {
                        System.out.println(ColorCodes.YELLOW + "Admin login failed or user chose to go back." + ColorCodes.RESET);
                    }
                }
                case 2 -> {
                    TravelAgent loggedInAgent = app.processTravelAgentLogin();
                    if (loggedInAgent != null) {
                        System.out.println(ColorCodes.GREEN + "Travel Agent login successful! Welcome, " + loggedInAgent.getUsername() + "!" + ColorCodes.RESET);
                        app.showTravelAgentMenu(loggedInAgent);
                    } else {
                        System.out.println(ColorCodes.YELLOW + "Travel Agent login failed or user chose to go back." + ColorCodes.RESET);
                    }
                }
                case 3 -> {
                    Customer loggedInCustomer = app.processCustomerLogin();
                    if (loggedInCustomer != null) {
                        System.out.println(ColorCodes.GREEN + "Customer login successful! Welcome, " + loggedInCustomer.getUsername() + "!" + ColorCodes.RESET);
                        app.showCustomerMenu(loggedInCustomer);
                    } else {
                        System.out.println(ColorCodes.YELLOW + "Customer login failed or user chose to go back." + ColorCodes.RESET);
                    }
                }
                case 4 -> {
                    running = false;
                    System.out.println(ColorCodes.WHITE + "Exiting Travel Management System. Goodbye!" + ColorCodes.RESET);
                }
                default -> {
                    System.out.println(ColorCodes.RED + "Invalid choice. Please try again." + ColorCodes.RESET);
                }
            }
            System.out.println();
        }
        sc.close(); // Close the scanner
        DBMS.disconnect();
    }

    public Admin processAdminLogin() {
        while (true) {
            System.out.println(ColorCodes.BOLD + ColorCodes.WHITE + "\n--- Admin Menu ---" + ColorCodes.RESET);
            System.out.println(ColorCodes.YELLOW + "Press 1 " + ColorCodes.RESET + "→ " + ColorCodes.GREEN + "Log In" + ColorCodes.RESET);
            System.out.println(ColorCodes.YELLOW + "Press 2 " + ColorCodes.RESET + "→ " + ColorCodes.BLUE + "Sign Up" + ColorCodes.RESET);
            System.out.println(ColorCodes.YELLOW + "Press 3 " + ColorCodes.RESET + "→ " + ColorCodes.RED + "Back to Main Menu" + ColorCodes.RESET);
            System.out.print(ColorCodes.BOLD + ColorCodes.CYAN + "Enter your choice: " + ColorCodes.RESET);
            int admin_choice = -1;
            try {
                admin_choice = sc.nextInt();
            } catch (InputMismatchException e) {
                System.out.println(ColorCodes.RED + "Invalid input. Please enter a number." + ColorCodes.RESET);
                sc.next();
                continue;
            }
            sc.nextLine();

            switch (admin_choice) {
                case 1 -> {
                    System.out.print(ColorCodes.CYAN + "Enter Admin Username: " + ColorCodes.RESET);
                    String username = sc.nextLine();
                    System.out.print(ColorCodes.CYAN + "Enter Admin Password: " + ColorCodes.RESET);
                    String password = sc.nextLine();
                    Admin admin = Admin.login(username, password, con);
                    if (admin != null) {
                        return admin;
                    } else {
                        System.out.println(ColorCodes.RED + "Invalid Admin credentials." + ColorCodes.RESET);
                    }
                }
                case 2 -> {
                    System.out.print(ColorCodes.CYAN + "Enter New Admin Username: " + ColorCodes.RESET);
                    String newUsername = sc.nextLine();
                    System.out.print(ColorCodes.CYAN + "Enter New Admin Password: " + ColorCodes.RESET);
                    String newPassword = sc.nextLine();
                    if (Admin.signUp(newUsername, newPassword, con)) {
                        System.out.println(ColorCodes.GREEN + "Admin account created successfully! Please log in." + ColorCodes.RESET);
                    } else {
                        System.out.println(ColorCodes.YELLOW + "Failed to create Admin account. Username might already exist." + ColorCodes.RESET);
                    }
                }
                case 3 -> {
                    return null;
                }
                default -> {
                    System.out.println(ColorCodes.RED + "Invalid choice. Please try again." + ColorCodes.RESET);
                }
            }
        }
    }

    public TravelAgent processTravelAgentLogin() {
        while (true) {
            System.out.println(ColorCodes.BOLD + ColorCodes.WHITE + "\n--- Travel Agent Menu ---" + ColorCodes.RESET);
            System.out.println(ColorCodes.YELLOW + "Press 1 " + ColorCodes.RESET + "→ " + ColorCodes.GREEN + "Log In" + ColorCodes.RESET);
            System.out.println(ColorCodes.YELLOW + "Press 2 " + ColorCodes.RESET + "→ " + ColorCodes.BLUE + "Sign Up" + ColorCodes.RESET);
            System.out.println(ColorCodes.YELLOW + "Press 3 " + ColorCodes.RESET + "→ " + ColorCodes.RED + "Back to Main Menu" + ColorCodes.RESET);
            System.out.print(ColorCodes.BOLD + ColorCodes.CYAN + "Enter your choice: " + ColorCodes.RESET);
            int agent_choice = -1;
            try {
                agent_choice = sc.nextInt();
            } catch (InputMismatchException e) {
                System.out.println(ColorCodes.RED + "Invalid input. Please enter a number." + ColorCodes.RESET);
                sc.next();
                continue;
            }
            sc.nextLine();

            switch (agent_choice) {
                case 1 -> {
                    System.out.print(ColorCodes.CYAN + "Enter Travel Agent Username: " + ColorCodes.RESET);
                    String username = sc.nextLine();
                    System.out.print(ColorCodes.CYAN + "Enter Travel Agent Password: " + ColorCodes.RESET);
                    String password = sc.nextLine();
                    TravelAgent agent = TravelAgent.login(username, password, con);
                    if (agent != null) {
                        return agent;
                    } else {
                        System.out.println(ColorCodes.RED + "Invalid Travel Agent credentials." + ColorCodes.RESET);
                    }
                }
                case 2 -> {
                    System.out.print(ColorCodes.CYAN + "Enter New Travel Agent Username: " + ColorCodes.RESET);
                    String newUsername = sc.nextLine();
                    System.out.print(ColorCodes.CYAN + "Enter New Travel Agent Password: " + ColorCodes.RESET);
                    String newPassword = sc.nextLine();
                    if (TravelAgent.signUp(newUsername, newPassword, con)) {
                        System.out.println(ColorCodes.GREEN + "Travel Agent account created successfully! Please log in." + ColorCodes.RESET);
                    } else {
                        System.out.println(ColorCodes.YELLOW + "Failed to create Travel Agent account. Username might already exist." + ColorCodes.RESET);
                    }
                }
                case 3 -> {
                    return null;
                }
                default -> {
                    System.out.println(ColorCodes.RED + "Invalid choice. Please try again." + ColorCodes.RESET);
                }
            }
        }
    }

    public Customer processCustomerLogin() {
        while (true) {
            System.out.println(ColorCodes.BOLD + ColorCodes.WHITE + "\n--- Customer Menu ---" + ColorCodes.RESET);
            System.out.println(ColorCodes.YELLOW + "Press 1 " + ColorCodes.RESET + "→ " + ColorCodes.GREEN + "Log In" + ColorCodes.RESET);
            System.out.println(ColorCodes.YELLOW + "Press 2 " + ColorCodes.RESET + "→ " + ColorCodes.BLUE + "Sign Up" + ColorCodes.RESET);
            System.out.println(ColorCodes.YELLOW + "Press 3 " + ColorCodes.RESET + "→ " + ColorCodes.RED + "Back to Main Menu" + ColorCodes.RESET);
            System.out.print(ColorCodes.BOLD + ColorCodes.CYAN + "Enter your choice: " + ColorCodes.RESET);
            int customer_choice = -1;
            try {
                customer_choice = sc.nextInt();
            } catch (InputMismatchException e) {
                System.out.println(ColorCodes.RED + "Invalid input. Please enter a number." + ColorCodes.RESET);
                sc.next();
                continue;
            }
            sc.nextLine();

            switch (customer_choice) {
                case 1 -> {
                    System.out.print(ColorCodes.CYAN + "Enter Customer Username: " + ColorCodes.RESET);
                    String username = sc.nextLine();
                    System.out.print(ColorCodes.CYAN + "Enter Customer Password: " + ColorCodes.RESET);
                    String password = sc.nextLine();
                    Customer customer = Customer.login(username, password, con);
                    if (customer != null) {
                        return customer;
                    } else {
                        System.out.println(ColorCodes.RED + "Invalid Customer credentials." + ColorCodes.RESET);
                    }
                }
                case 2 -> {
                    System.out.print(ColorCodes.CYAN + "Enter New Customer Username: " + ColorCodes.RESET);
                    String newUsername = sc.nextLine();
                    System.out.print(ColorCodes.CYAN + "Enter New Customer Password: " + ColorCodes.RESET);
                    String newPassword = sc.nextLine();
                    System.out.print(ColorCodes.CYAN + "Enter New Customer Email: " + ColorCodes.RESET);
                    String newEmail = sc.nextLine();
                    System.out.print(ColorCodes.CYAN + "Enter New Customer Phone Number: " + ColorCodes.RESET);
                    String newPhone = sc.nextLine();
                    if (Customer.signUp(newUsername, newPassword, newEmail, newPhone, con)) {
                        System.out.println(ColorCodes.GREEN + "Customer account created successfully! Please log in." + ColorCodes.RESET);
                    } else {
                        System.out.println(ColorCodes.YELLOW + "Failed to create Customer account. Username might already exist." + ColorCodes.RESET);
                    }
                }
                case 3 -> {
                    return null;
                }
                default -> {
                    System.out.println(ColorCodes.RED + "Invalid choice. Please try again." + ColorCodes.RESET);
                }
            }
        }
    }

    public void showAdminMenu(Admin admin) {
        boolean adminMenuRunning = true;
        while (adminMenuRunning) {
            System.out.println(ColorCodes.BOLD + ColorCodes.CYAN + "\n=== Admin Dashboard (" + admin.getUsername() + ") ===" + ColorCodes.RESET);
            System.out.println(ColorCodes.YELLOW + "1. " + ColorCodes.RESET + ColorCodes.WHITE + "Manage Tours" + ColorCodes.RESET);
            System.out.println(ColorCodes.YELLOW + "2. " + ColorCodes.RESET + ColorCodes.WHITE + "Manage Users" + ColorCodes.RESET);
            System.out.println(ColorCodes.YELLOW + "3. " + ColorCodes.RESET + ColorCodes.WHITE + "View Reports" + ColorCodes.RESET);
            System.out.println(ColorCodes.YELLOW + "4. " + ColorCodes.RESET + ColorCodes.WHITE + "Manage Admin Profile" + ColorCodes.RESET);
            System.out.println(ColorCodes.YELLOW + "5. " + ColorCodes.RESET + ColorCodes.RED + "Logout" + ColorCodes.RESET);
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

            switch (choice) {
                case 1 -> admin.manageTours(con, sc);
                case 2 -> admin.manageUsers(con, sc);
                case 3 -> admin.viewReports(con);
                case 4 -> admin.manageAdminProfile(con, sc);
                case 5 -> {
                    System.out.println(ColorCodes.WHITE + "Logging out " + admin.getUsername() + "..." + ColorCodes.RESET);
                    adminMenuRunning = false;
                }
                default -> System.out.println(ColorCodes.RED + "Invalid choice. Please try again." + ColorCodes.RESET);
            }
        }
    }

    public void showTravelAgentMenu(TravelAgent agent) {
        boolean agentMenuRunning = true;
        while (agentMenuRunning) {
            System.out.println(ColorCodes.BOLD + ColorCodes.CYAN + "\n=== Travel Agent Dashboard (" + agent.getUsername() + ") ===" + ColorCodes.RESET);
            System.out.println(ColorCodes.YELLOW + "1. " + ColorCodes.RESET + ColorCodes.WHITE + "View Available Tours" + ColorCodes.RESET);
            System.out.println(ColorCodes.YELLOW + "2. " + ColorCodes.RESET + ColorCodes.WHITE + "Book Tour for Customer" + ColorCodes.RESET);
            System.out.println(ColorCodes.YELLOW + "3. " + ColorCodes.RESET + ColorCodes.WHITE + "View My Bookings" + ColorCodes.RESET);
            System.out.println(ColorCodes.YELLOW + "4. " + ColorCodes.RESET + ColorCodes.WHITE + "Manage Agent Profile" + ColorCodes.RESET);
            System.out.println(ColorCodes.YELLOW + "5. " + ColorCodes.RESET + ColorCodes.RED + "Logout" + ColorCodes.RESET);
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

            switch (choice) {
                case 1 -> agent.viewAvailableTours(con);
                case 2 -> agent.bookTourForCustomer(con, sc);
                case 3 -> agent.viewMyBookings(con);
                case 4 -> agent.manageAgentProfile(con, sc);
                case 5 -> {
                    System.out.println(ColorCodes.WHITE + "Logging out " + agent.getUsername() + "..." + ColorCodes.RESET);
                    agentMenuRunning = false;
                }
                default -> System.out.println(ColorCodes.RED + "Invalid choice. Please try again." + ColorCodes.RESET);
            }
        }
    }

    public void showCustomerMenu(Customer customer) {
        boolean customerMenuRunning = true;
        while (customerMenuRunning) {
            System.out.println(ColorCodes.BOLD + ColorCodes.CYAN + "\n=== Customer Dashboard (" + customer.getUsername() + ") ===" + ColorCodes.RESET);
            System.out.println(ColorCodes.YELLOW + "1. " + ColorCodes.RESET + ColorCodes.WHITE + "Browse Tours" + ColorCodes.RESET);
            System.out.println(ColorCodes.YELLOW + "2. " + ColorCodes.RESET + ColorCodes.WHITE + "Book a Tour" + ColorCodes.RESET);
            System.out.println(ColorCodes.YELLOW + "3. " + ColorCodes.RESET + ColorCodes.WHITE + "View My Bookings" + ColorCodes.RESET);
            System.out.println(ColorCodes.YELLOW + "4. " + ColorCodes.RESET + ColorCodes.WHITE + "Update Profile" + ColorCodes.RESET);
            System.out.println(ColorCodes.YELLOW + "5. " + ColorCodes.RESET + ColorCodes.RED + "Logout" + ColorCodes.RESET);
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

            switch (choice) {
                case 1 -> customer.browseTours(con);
                case 2 -> customer.bookTour(con, sc);
                case 3 -> customer.viewMyBookings(con);
                case 4 -> customer.updateProfile(con, sc);
                case 5 -> {
                    System.out.println(ColorCodes.WHITE + "Logging out " + customer.getUsername() + "..." + ColorCodes.RESET);
                    customerMenuRunning = false;
                }
                default -> System.out.println(ColorCodes.RED + "Invalid choice. Please try again." + ColorCodes.RESET);
            }
        }
    }
}