package dao;

import model.Booking;
import model.UserBookingView;
import config.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookingDaoSql {

    public boolean createBooking(Booking booking) {
        String sql = "INSERT INTO bookings (user_id, court_id, start_time, end_time, total_price, status, payment_status, payment_reference) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, booking.getUserId());
            stmt.setInt(2, booking.getCourtId());
            stmt.setTimestamp(3, Timestamp.valueOf(booking.getStartTime()));
            stmt.setTimestamp(4, Timestamp.valueOf(booking.getEndTime()));
            stmt.setDouble(5, booking.getTotalPrice());
            stmt.setString(6, booking.getStatus() != null ? booking.getStatus() : "CONFIRMED");
            stmt.setString(7, booking.getPaymentStatus() != null ? booking.getPaymentStatus() : "PAID");
            stmt.setString(8, booking.getPaymentReference());

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

    public List<UserBookingView> getBookingsByUserId(int userId) {
        String sql = """
                SELECT b.id, b.court_id, c.court_name, c.location, c.court_type,
                       b.start_time, b.end_time, b.total_price, b.status, b.payment_status
                FROM bookings b
                JOIN courts c ON b.court_id = c.id
                WHERE b.user_id = ?
                ORDER BY b.start_time DESC
                """;

        List<UserBookingView> bookings = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    UserBookingView booking = new UserBookingView();
                    booking.setId(rs.getInt("id"));
                    booking.setCourtId(rs.getInt("court_id"));
                    booking.setCourtName(rs.getString("court_name"));
                    booking.setLocation(rs.getString("location"));
                    booking.setCourtType(rs.getString("court_type"));
                    booking.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
                    booking.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
                    booking.setTotalPrice(rs.getDouble("total_price"));
                    booking.setStatus(rs.getString("status"));
                    String paymentStatus = rs.getString("payment_status");
                    booking.setPaymentStatus(paymentStatus != null ? paymentStatus : "PAID");
                    bookings.add(booking);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bookings;
    }
}