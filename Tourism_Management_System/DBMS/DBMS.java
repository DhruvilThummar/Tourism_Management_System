package src.Tourism_Management_System.DBMS;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import src.Tourism_Management_System.ColorCodes;

public class DBMS {
    public static Connection con = null;

    public static void connect() {
        if (con == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                con = DriverManager.getConnection("jdbc:mysql://localhost:3306/tourism_db", "root", "");
                System.out.println(ColorCodes.GREEN + "Database connected successfully!" + ColorCodes.RESET);
            } catch (ClassNotFoundException e) {
                System.err.println(ColorCodes.RED + "JDBC Driver not found: " + e.getMessage() + ColorCodes.RESET);
            } catch (SQLException e) {
                System.err.println(ColorCodes.RED + "Database connection error: " + e.getMessage() + ColorCodes.RESET);
            }
        }
    }

    public static void disconnect() {
        if (con != null) {
            try {
                con.close();
                con = null;
                System.out.println(ColorCodes.WHITE + "Database disconnected." + ColorCodes.RESET);
            } catch (SQLException e) {
                System.err.println(ColorCodes.RED + "Error closing database connection: " + e.getMessage() + ColorCodes.RESET);
            }
        }
    }

    // Centralized username existence check
    public static boolean usernameExists(String username, String tableName, Connection con) {
        String query = "SELECT 1 FROM " + tableName + " WHERE username = ?";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, username);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println(ColorCodes.RED + "Error checking username existence in " + tableName + ": " + e.getMessage() + ColorCodes.RESET);
            return false;
        }
    }
}
