package dao;

import config.DBConnection;
import model.MatchAnnouncement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MatchAnnouncementDaoSql implements MatchAnnouncementDao {
    @Override
    public int createMatch(MatchAnnouncement match) {
        String sql = "INSERT INTO match_announcements " +
                "(user_id, venue, match_time, sport_type, players_needed, notes, skill_level, contact_info) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, match.getUserId());
            stmt.setString(2, match.getVenue());
            stmt.setTimestamp(3, match.getMatchTime());
            stmt.setString(4, match.getSportType());
            stmt.setInt(5, match.getPlayersNeeded());
            stmt.setString(6, match.getNotes());
            stmt.setString(7, match.getSkillLevel());
            stmt.setString(8, match.getContactInfo());

            if (stmt.executeUpdate() == 0) {
                return -1;
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
            return -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public List<MatchAnnouncement> getAllMatches(int currentUserId) {
        String sql = """
                SELECT m.*,
                    CASE WHEN mj.user_id IS NULL THEN FALSE ELSE TRUE END AS joined_by_current_user
                FROM match_announcements m
                LEFT JOIN match_joins mj ON m.id = mj.match_id AND mj.user_id = ?
                ORDER BY m.match_time ASC
                """;
        List<MatchAnnouncement> matches = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, currentUserId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MatchAnnouncement match = mapRow(rs);
                    match.setJoinedByCurrentUser(rs.getBoolean("joined_by_current_user"));
                    matches.add(match);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return matches;
    }

    @Override
    public MatchAnnouncement getMatchById(int id) {
        String sql = "SELECT * FROM match_announcements WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean updateMatch(MatchAnnouncement match) {
        String sql = "UPDATE match_announcements SET venue = ?, match_time = ?, sport_type = ?, " +
                "players_needed = ?, notes = ?, skill_level = ?, contact_info = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, match.getVenue());
            stmt.setTimestamp(2, match.getMatchTime());
            stmt.setString(3, match.getSportType());
            stmt.setInt(4, match.getPlayersNeeded());
            stmt.setString(5, match.getNotes());
            stmt.setString(6, match.getSkillLevel());
            stmt.setString(7, match.getContactInfo());
            stmt.setInt(8, match.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteMatch(int id) {
        String sql = "DELETE FROM match_announcements WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean joinMatch(int matchId, int userId) {
        String insertJoin = "INSERT INTO match_joins (match_id, user_id) VALUES (?, ?)";
        String updateOpenSpots = "UPDATE match_announcements SET players_needed = players_needed - 1 " +
                "WHERE id = ? AND players_needed > 0";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement insertStmt = conn.prepareStatement(insertJoin);
                 PreparedStatement updateStmt = conn.prepareStatement(updateOpenSpots)) {

                insertStmt.setInt(1, matchId);
                insertStmt.setInt(2, userId);
                insertStmt.executeUpdate();

                updateStmt.setInt(1, matchId);
                if (updateStmt.executeUpdate() == 0) {
                    conn.rollback();
                    return false;
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private MatchAnnouncement mapRow(ResultSet rs) throws SQLException {
        return new MatchAnnouncement(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getString("venue"),
                rs.getTimestamp("match_time"),
                rs.getString("sport_type"),
                rs.getInt("players_needed"),
                rs.getString("notes"),
                rs.getString("skill_level"),
                rs.getString("contact_info"),
                rs.getTimestamp("created_at")
        );
    }
}
