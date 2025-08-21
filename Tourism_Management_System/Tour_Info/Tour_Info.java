package src.Tourism_Management_System.Tour_Info;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import src.Tourism_Management_System.ColorCodes;


public class Tour_Info {

    // --- Member Variables ---
    private String tourId;
    private String tourName;
    private String destination;
    private int durationInDays;
    private double price;
    private String description;
    private int maxGroupSize;
    private int availableSlots;
    private List<String> availableSites;

    // --- Constructors ---
    public Tour_Info() {
        this.availableSites = new ArrayList<>();
    }

    public Tour_Info(String tourId, String tourName, String destination, int durationInDays, double price, String description, int maxGroupSize, List<String> availableSites) {
        this.tourId = tourId;
        this.tourName = tourName;
        this.destination = destination;
        this.durationInDays = durationInDays;
        this.price = price;
        this.description = description;
        this.maxGroupSize = maxGroupSize;
        this.availableSlots = maxGroupSize; // Initialize available slots to max group size for new tours
        this.availableSites = (availableSites != null) ? new ArrayList<>(availableSites) : new ArrayList<>();
    }

    // --- Getters and Setters ---
    public String getTourId() { return tourId; }
    public void setTourId(String tourId) { this.tourId = tourId; }
    public String getTourName() { return tourName; }
    public void setTourName(String tourName) { this.tourName = tourName; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public int getDurationInDays() { return durationInDays; }
    public void setDurationInDays(int durationInDays) { if (durationInDays > 0) this.durationInDays = durationInDays; }
    public double getPrice() { return price; }
    public void setPrice(double price) { if (price >= 0) this.price = price; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getMaxGroupSize() { return maxGroupSize; }
    public void setMaxGroupSize(int maxGroupSize) {
        if (maxGroupSize > 0) {
            this.maxGroupSize = maxGroupSize;
        }
    }
    public int getAvailableSlots() { return availableSlots; }
    public void setAvailableSlots(int availableSlots) { this.availableSlots = availableSlots; }
    public List<String> getAvailableSites() { return new ArrayList<>(availableSites); }
    public void setAvailableSites(List<String> availableSites) { this.availableSites = (availableSites != null) ? new ArrayList<>(availableSites) : new ArrayList<>(); }

    // --- Agent/Booking and Database Operations ---

    public boolean bookTour(Connection con) throws SQLException {
        if (this.availableSlots > 0) {

            this.availableSlots--;
            this.update(con); // Update the tour's available slots in the DB
            return true;
        }
        return false;
    }

    public boolean cancelBooking(Connection con) throws SQLException {
        if (this.availableSlots < this.maxGroupSize) {
            this.availableSlots++;
            this.update(con);
            return true;
        }
        return false;
    }

    public void save(Connection con) throws SQLException {
        String sql = "INSERT INTO tour (tour_id, tour_name, destination, duration_in_days, price, description, max_group_size, available_slots, available_sites) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, this.tourId);
            pstmt.setString(2, this.tourName);
            pstmt.setString(3, this.destination);
            pstmt.setInt(4, this.durationInDays);
            pstmt.setDouble(5, this.price);
            pstmt.setString(6, this.description);
            pstmt.setInt(7, this.maxGroupSize);
            pstmt.setInt(8, this.availableSlots);
            String sitesStr = String.join(",", this.availableSites);
            pstmt.setString(9, sitesStr);
            pstmt.executeUpdate();
        }
    }

    public void update(Connection con) throws SQLException {
        String sql = "UPDATE tour SET tour_name = ?, destination = ?, duration_in_days = ?, price = ?, description = ?, max_group_size = ?, available_slots = ?, available_sites = ? WHERE tour_id = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, this.tourName);
            pstmt.setString(2, this.destination);
            pstmt.setInt(3, this.durationInDays);
            pstmt.setDouble(4, this.price);
            pstmt.setString(5, this.description);
            pstmt.setInt(6, this.maxGroupSize);
            pstmt.setInt(7, this.availableSlots);
            String sitesStr = String.join(",", this.availableSites);
            pstmt.setString(8, sitesStr);
            pstmt.setString(9, this.tourId);
            pstmt.executeUpdate();
        }
    }

    public void delete(Connection con) throws SQLException {
        String sql = "DELETE FROM tour WHERE tour_id = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, this.tourId);
            pstmt.executeUpdate();
        }
    }

    // Static cache for tours
    public static Map<String, Tour_Info> tourCache = new HashMap<>();

    // Modify the load method to use the cache
    public static Tour_Info load(Connection con, String tourId) throws SQLException {
        // If tour is in cache, return it
        if (tourCache.containsKey(tourId)) {
            System.out.println("(Retrieved " + tourId + " from cache)");
            return tourCache.get(tourId);
        }

        // Otherwise, fetch from DB
        String sql = "SELECT * FROM tour WHERE tour_id = ?";
        Tour_Info tour = null;
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, tourId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    tour = createTourFromResultSet(rs);
                }
            }
        }
        if (tour != null) {
            tourCache.put(tourId, tour); // Add the newly loaded tour to the cache
        }
        return tour;
    }

    // A method to clear cache if tour details are updated by an admin
    public static void clearCache() {
        tourCache.clear();
    }


    public static List<Tour_Info> getAllTours(Connection con) throws SQLException {
        String sql = "SELECT * FROM tour";
        List<Tour_Info> tours = new ArrayList<>();
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tours.add(createTourFromResultSet(rs));
            }
        }
        return tours;
    }

    private static Tour_Info createTourFromResultSet(ResultSet rs) throws SQLException {
        String sitesStr = rs.getString("available_sites");
        List<String> sites = new ArrayList<>();
        if (sitesStr != null && !sitesStr.isEmpty()) {
            sites = Arrays.asList(sitesStr.split(","));
        }
        Tour_Info tour = new Tour_Info(
                rs.getString("tour_id"),
                rs.getString("tour_name"),
                rs.getString("destination"),
                rs.getInt("duration_in_days"),
                rs.getDouble("price"),
                rs.getString("description"),
                rs.getInt("max_group_size"),
                sites
        );
        tour.setAvailableSlots(rs.getInt("available_slots"));
        return tour;
    }



    // --- Overridden Methods ---
    @Override
    public String toString() {
        String slotsColor = availableSlots > 5 ? ColorCodes.GREEN : (availableSlots > 0 ? ColorCodes.YELLOW : ColorCodes.RED);
        return  ColorCodes.BLUE + ColorCodes.BOLD + " --- Tour Details --- " + ColorCodes.RESET + "\n" +
                ColorCodes.YELLOW + "  ID: " + ColorCodes.WHITE + tourId + ColorCodes.RESET + "\n" +
                ColorCodes.YELLOW + "  Name: " + ColorCodes.WHITE + tourName + ColorCodes.RESET + "\n" +
                ColorCodes.YELLOW + "  Destination: " + ColorCodes.WHITE + destination + ColorCodes.RESET + "\n" +
                ColorCodes.YELLOW + "  Duration: " + ColorCodes.WHITE + durationInDays + " days" + ColorCodes.RESET + "\n" +
                ColorCodes.YELLOW + "  Price: " + ColorCodes.GREEN + "$" + String.format("%.2f", price) + ColorCodes.RESET + "\n" +
                ColorCodes.YELLOW + "  Max Group Size: " + ColorCodes.WHITE + maxGroupSize + ColorCodes.RESET + "\n" +
                ColorCodes.YELLOW + "  Available Slots: " + slotsColor + ColorCodes.BOLD + availableSlots + ColorCodes.RESET + "\n" +
                ColorCodes.YELLOW + "  Sites: " + ColorCodes.CYAN + String.join(", ", availableSites) + ColorCodes.RESET + "\n" + // Joined for better display
                ColorCodes.YELLOW + "  Description: " + ColorCodes.WHITE + description + ColorCodes.RESET;
    }
}
