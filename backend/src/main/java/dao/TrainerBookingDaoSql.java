package dao;

import config.DBConnection;
import model.TrainerBooking;
import model.TrainerBookingView;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TrainerBookingDaoSql implements TrainerBookingDao {

    private static final String BASE_SELECT = """
            SELECT tb.id, tb.trainer_id, tb.trainer_venue_id, tb.player_id,
                   tb.requested_date, tb.requested_time_slot, tb.status, tb.created_at,
                   u.full_name AS player_name,
                   CONCAT(t.first_name, ' ', t.last_name) AS trainer_name,
                   tv.venue_name, tv.address AS venue_address, tv.price_override,
                   t.sport_type, t.price_per_session
            FROM trainer_bookings tb
            JOIN users u ON tb.player_id = u.id
            JOIN trainers t ON tb.trainer_id = t.id
            JOIN trainer_venues tv ON tb.trainer_venue_id = tv.id
            """;

    @Override
    public int createBooking(TrainerBooking booking) {
        String sql = "INSERT INTO trainer_bookings (trainer_id, trainer_venue_id, player_id, " +
                "requested_date, requested_time_slot, status) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, booking.getTrainerId());
            stmt.setInt(2, booking.getTrainerVenueId());
            stmt.setInt(3, booking.getPlayerId());
            stmt.setDate(4, Date.valueOf(booking.getRequestedDate()));
            stmt.setString(5, booking.getRequestedTimeSlot());
            stmt.setString(6, booking.getStatus() != null ? booking.getStatus() : "PENDING");

            if (stmt.executeUpdate() > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        return keys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public boolean updateStatus(int bookingId, int trainerId, String status) {
        String sql = "UPDATE trainer_bookings SET status = ? WHERE id = ? AND trainer_id = ? AND status = 'PENDING'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, bookingId);
            stmt.setInt(3, trainerId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<TrainerBookingView> getBookingsByPlayerId(int playerId) {
        return queryBookings(BASE_SELECT + " WHERE tb.player_id = ? ORDER BY tb.created_at DESC", playerId);
    }

    @Override
    public List<TrainerBookingView> getBookingsByTrainerId(int trainerId) {
        return queryBookings(BASE_SELECT + " WHERE tb.trainer_id = ? ORDER BY tb.created_at DESC", trainerId);
    }

    @Override
    public TrainerBookingView getBookingById(int bookingId) {
        List<TrainerBookingView> results = queryBookings(BASE_SELECT + " WHERE tb.id = ?", bookingId);
        return results.isEmpty() ? null : results.get(0);
    }

    private List<TrainerBookingView> queryBookings(String sql, int param) {
        List<TrainerBookingView> bookings = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, param);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapView(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    private TrainerBookingView mapView(ResultSet rs) throws SQLException {
        TrainerBookingView view = new TrainerBookingView();
        view.setId(rs.getInt("id"));
        view.setTrainerId(rs.getInt("trainer_id"));
        view.setTrainerVenueId(rs.getInt("trainer_venue_id"));
        view.setPlayerId(rs.getInt("player_id"));
        view.setPlayerName(rs.getString("player_name"));
        view.setTrainerName(rs.getString("trainer_name"));
        view.setVenueName(rs.getString("venue_name"));
        view.setVenueAddress(rs.getString("venue_address"));
        view.setSportType(rs.getString("sport_type"));

        Date requestedDate = rs.getDate("requested_date");
        if (requestedDate != null) {
            view.setRequestedDate(requestedDate.toLocalDate());
        }

        view.setRequestedTimeSlot(rs.getString("requested_time_slot"));
        view.setStatus(rs.getString("status"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            view.setCreatedAt(createdAt.toLocalDateTime());
        }

        double priceOverride = rs.getDouble("price_override");
        if (!rs.wasNull()) {
            view.setSessionPrice(priceOverride);
        } else {
            view.setSessionPrice(rs.getDouble("price_per_session"));
        }

        return view;
    }
}
