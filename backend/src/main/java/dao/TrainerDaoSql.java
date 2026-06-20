package dao;

import config.DBConnection;
import model.Trainer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TrainerDaoSql implements TrainerDao {

    @Override
    public boolean addTrainer(Trainer trainer) {
        String sql = "INSERT INTO trainers (user_id, first_name, last_name, phone, sport_type, price_per_session) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, trainer.getUserId());
            stmt.setString(2, trainer.getFirstName());
            stmt.setString(3, trainer.getLastName());
            stmt.setString(4, trainer.getPhone());
            stmt.setString(5, trainer.getSportType());
            stmt.setDouble(6, trainer.getPricePerSession());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateRating(int trainerId, double newRating, int newReviewCount) {
        String sql = "UPDATE trainers SET rating = ?, review_count = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, newRating);
            stmt.setInt(2, newReviewCount);
            stmt.setInt(3, trainerId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Trainer> getAllTrainers() {
        String sql = "SELECT * FROM trainers";
        List<Trainer> trainers = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                trainers.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trainers;
    }

    @Override
    public Trainer getTrainerById(int id) {
        String sql = "SELECT * FROM trainers WHERE id = ?";

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

    private Trainer mapRow(ResultSet rs) throws SQLException {
        return new Trainer(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("phone"),
                rs.getString("sport_type"),
                rs.getDouble("price_per_session"),
                rs.getDouble("rating"),
                rs.getInt("review_count")
        );
    }
}
