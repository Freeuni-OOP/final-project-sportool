package dao;

import config.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TrainerProfileDaoSql implements TrainerProfileDao {

    @Override
    public String getDescription(int trainerId) {
        String sql = "SELECT description FROM trainer_profiles WHERE trainer_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, trainerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("description");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean upsertDescription(int trainerId, String description) {
        String sql = """
                INSERT INTO trainer_profiles (trainer_id, description, updated_at)
                VALUES (?, ?, CURRENT_TIMESTAMP)
                ON CONFLICT (trainer_id)
                DO UPDATE SET description = EXCLUDED.description, updated_at = CURRENT_TIMESTAMP
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, trainerId);
            stmt.setString(2, description);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

