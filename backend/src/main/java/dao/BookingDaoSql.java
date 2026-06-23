package dao;

import model.Booking;
import config.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookingDaoSql {

    public boolean createBooking(Booking booking) {
        String sql = "INSERT INTO bookings (user_id, court_id, start_time, end_time, total_price, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, booking.getUserId());
            stmt.setInt(2, booking.getCourtId());
            stmt.setTimestamp(3, Timestamp.valueOf(booking.getStartTime()));
            stmt.setTimestamp(4, Timestamp.valueOf(booking.getEndTime()));
            stmt.setDouble(5, booking.getTotalPrice());
            stmt.setString(6, booking.getStatus() != null ? booking.getStatus() : "CONFIRMED");

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        booking.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isCourtAvailable(int courtId, LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT COUNT(*) FROM bookings " +
                "WHERE court_id = ? " +
                "AND status = 'CONFIRMED' " +
                "AND ? < end_time AND ? > start_time";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, courtId);
            stmt.setTimestamp(2, Timestamp.valueOf(start));
            stmt.setTimestamp(3, Timestamp.valueOf(end));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count == 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Booking> getBookingsForCourtOnDate(int courtId, LocalDate date) {
        String sql = "SELECT id, user_id, court_id, start_time, end_time, total_price, status " +
                "FROM bookings " +
                "WHERE court_id = ? " +
                "AND status = 'CONFIRMED' " +
                "AND start_time >= ? " +
                "AND start_time < ?";

        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();
        List<Booking> bookings = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, courtId);
            stmt.setTimestamp(2, Timestamp.valueOf(dayStart));
            stmt.setTimestamp(3, Timestamp.valueOf(dayEnd));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bookings.add(new Booking(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getInt("court_id"),
                            rs.getTimestamp("start_time").toLocalDateTime(),
                            rs.getTimestamp("end_time").toLocalDateTime(),
                            rs.getDouble("total_price"),
                            rs.getString("status")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bookings;
    }
}