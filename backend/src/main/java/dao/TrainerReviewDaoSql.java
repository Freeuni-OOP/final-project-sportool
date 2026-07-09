package dao;

import config.DBConnection;
import model.TrainerReview;
import model.TrainerReviewView;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TrainerReviewDaoSql implements TrainerReviewDao {

    @Override
    public int createReview(TrainerReview review) {
        String sql = "INSERT INTO trainer_reviews (trainer_id, player_id, rating, comment) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            int reviewId = -1;
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, review.getTrainerId());
                stmt.setInt(2, review.getPlayerId());
                stmt.setInt(3, review.getRating());
                stmt.setString(4, review.getComment());

                if (stmt.executeUpdate() > 0) {
                    try (ResultSet keys = stmt.getGeneratedKeys()) {
                        if (keys.next()) {
                            reviewId = keys.getInt(1);
                        }
                    }
                }
            }

            if (reviewId > 0) {
                recomputeTrainerRating(conn, review.getTrainerId());
                conn.commit();
            } else {
                conn.rollback();
            }
            return reviewId;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public List<TrainerReviewView> getReviewsForTrainer(int trainerId) {
        String sql = """
                SELECT tr.id, tr.trainer_id, tr.player_id, tr.rating, tr.comment, tr.created_at,
                       u.full_name AS player_name
                FROM trainer_reviews tr
                JOIN users u ON tr.player_id = u.id
                WHERE tr.trainer_id = ?
                ORDER BY tr.created_at DESC
                """;

        List<TrainerReviewView> reviews = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, trainerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reviews.add(mapView(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    @Override
    public boolean trainerHasReviewFromPlayer(int trainerId, int playerId) {
        String sql = "SELECT 1 FROM trainer_reviews WHERE trainer_id = ? AND player_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, trainerId);
            stmt.setInt(2, playerId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void recomputeTrainerRating(Connection conn, int trainerId) throws SQLException {
        String sql = """
                UPDATE trainers
                SET rating = COALESCE((
                        SELECT AVG(rating)::numeric(10,2)
                        FROM trainer_reviews
                        WHERE trainer_id = ?
                    ), 0),
                    review_count = COALESCE((
                        SELECT COUNT(*)
                        FROM trainer_reviews
                        WHERE trainer_id = ?
                    ), 0)
                WHERE id = ?
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, trainerId);
            stmt.setInt(2, trainerId);
            stmt.setInt(3, trainerId);
            stmt.executeUpdate();
        }
    }

    private TrainerReviewView mapView(ResultSet rs) throws SQLException {
        TrainerReviewView view = new TrainerReviewView();
        view.setId(rs.getInt("id"));
        view.setTrainerId(rs.getInt("trainer_id"));
        view.setPlayerId(rs.getInt("player_id"));
        view.setPlayerName(rs.getString("player_name"));
        view.setRating(rs.getInt("rating"));
        view.setComment(rs.getString("comment"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            view.setCreatedAt(createdAt.toLocalDateTime());
        }
        return view;
    }
}

