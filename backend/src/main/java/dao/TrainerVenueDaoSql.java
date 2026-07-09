package dao;

import config.DBConnection;
import model.AvailabilitySlot;
import model.TrainerVenue;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrainerVenueDaoSql implements TrainerVenueDao {

    @Override
    public int createVenue(TrainerVenue venue) {
        String sql = "INSERT INTO trainer_venues (trainer_id, venue_name, address, price_override) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, venue.getTrainerId());
            stmt.setString(2, venue.getVenueName());
            stmt.setString(3, venue.getAddress());
            if (venue.getPriceOverride() != null) {
                stmt.setDouble(4, venue.getPriceOverride());
            } else {
                stmt.setNull(4, Types.NUMERIC);
            }

            if (stmt.executeUpdate() > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        int venueId = keys.getInt(1);
                        saveAvailability(conn, venueId, venue.getAvailability());
                        return venueId;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public boolean updateVenue(TrainerVenue venue) {
        String sql = "UPDATE trainer_venues SET venue_name = ?, address = ?, price_override = ? " +
                "WHERE id = ? AND trainer_id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, venue.getVenueName());
                stmt.setString(2, venue.getAddress());
                if (venue.getPriceOverride() != null) {
                    stmt.setDouble(3, venue.getPriceOverride());
                } else {
                    stmt.setNull(3, Types.NUMERIC);
                }
                stmt.setInt(4, venue.getId());
                stmt.setInt(5, venue.getTrainerId());

                if (stmt.executeUpdate() <= 0) {
                    conn.rollback();
                    return false;
                }
            }

            try (PreparedStatement deleteSlots = conn.prepareStatement(
                    "DELETE FROM trainer_availability WHERE trainer_venue_id = ?")) {
                deleteSlots.setInt(1, venue.getId());
                deleteSlots.executeUpdate();
            }

            saveAvailability(conn, venue.getId(), venue.getAvailability());
            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteVenue(int venueId, int trainerId) {
        String sql = "DELETE FROM trainer_venues WHERE id = ? AND trainer_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, venueId);
            stmt.setInt(2, trainerId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public TrainerVenue getVenueById(int venueId) {
        String sql = "SELECT * FROM trainer_venues WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, venueId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    TrainerVenue venue = mapVenue(rs);
                    venue.setAvailability(loadAvailability(conn, venueId));
                    return venue;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<TrainerVenue> getVenuesByTrainerId(int trainerId) {
        String sql = "SELECT * FROM trainer_venues WHERE trainer_id = ? ORDER BY id";
        List<TrainerVenue> venues = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, trainerId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<Integer> venueIds = new ArrayList<>();
                while (rs.next()) {
                    TrainerVenue venue = mapVenue(rs);
                    venues.add(venue);
                    venueIds.add(venue.getId());
                }

                Map<Integer, List<AvailabilitySlot>> slotsByVenue = loadAvailabilityForVenues(conn, venueIds);
                for (TrainerVenue venue : venues) {
                    venue.setAvailability(slotsByVenue.getOrDefault(venue.getId(), new ArrayList<>()));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return venues;
    }

    @Override
    public boolean venueBelongsToTrainer(int venueId, int trainerId) {
        String sql = "SELECT 1 FROM trainer_venues WHERE id = ? AND trainer_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, venueId);
            stmt.setInt(2, trainerId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void saveAvailability(Connection conn, int venueId, List<AvailabilitySlot> slots) throws SQLException {
        if (slots == null || slots.isEmpty()) {
            return;
        }

        String sql = "INSERT INTO trainer_availability (trainer_venue_id, day_of_week, start_time, end_time) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (AvailabilitySlot slot : slots) {
                stmt.setInt(1, venueId);
                stmt.setString(2, slot.getDayOfWeek());
                stmt.setString(3, slot.getStartTime());
                stmt.setString(4, slot.getEndTime());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    private List<AvailabilitySlot> loadAvailability(Connection conn, int venueId) throws SQLException {
        return loadAvailabilityForVenues(conn, List.of(venueId)).getOrDefault(venueId, new ArrayList<>());
    }

    private Map<Integer, List<AvailabilitySlot>> loadAvailabilityForVenues(Connection conn, List<Integer> venueIds)
            throws SQLException {
        Map<Integer, List<AvailabilitySlot>> result = new HashMap<>();
        if (venueIds.isEmpty()) {
            return result;
        }

        String placeholders = String.join(",", venueIds.stream().map(id -> "?").toList());
        String sql = "SELECT * FROM trainer_availability WHERE trainer_venue_id IN (" + placeholders + ") " +
                "ORDER BY trainer_venue_id, day_of_week, start_time";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < venueIds.size(); i++) {
                stmt.setInt(i + 1, venueIds.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int venueId = rs.getInt("trainer_venue_id");
                    AvailabilitySlot slot = new AvailabilitySlot();
                    slot.setId(rs.getInt("id"));
                    slot.setDayOfWeek(rs.getString("day_of_week"));
                    slot.setStartTime(rs.getString("start_time"));
                    slot.setEndTime(rs.getString("end_time"));
                    result.computeIfAbsent(venueId, key -> new ArrayList<>()).add(slot);
                }
            }
        }
        return result;
    }

    private TrainerVenue mapVenue(ResultSet rs) throws SQLException {
        TrainerVenue venue = new TrainerVenue();
        venue.setId(rs.getInt("id"));
        venue.setTrainerId(rs.getInt("trainer_id"));
        venue.setVenueName(rs.getString("venue_name"));
        venue.setAddress(rs.getString("address"));
        double priceOverride = rs.getDouble("price_override");
        if (!rs.wasNull()) {
            venue.setPriceOverride(priceOverride);
        }
        return venue;
    }
}
