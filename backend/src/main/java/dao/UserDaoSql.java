package dao;

import model.User;
import config.DBConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDaoSql implements UserDao {

    @Override
    public String registerUser(User user) {
        String sql = "INSERT INTO users (email, password_hash, role, full_name) VALUES (?, ?, ?, ?)";

        String hashedPassword = BCrypt.hashpw(user.getPasswordHash(), BCrypt.gensalt());

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getEmail());
            stmt.setString(2, hashedPassword);
            stmt.setString(3, user.getRole());
            stmt.setString(4, user.getFullName());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0 ? null : "Registration failed. No user was created.";

        } catch (SQLException e) {
            e.printStackTrace();
            return mapSqlError(e);
        }
    }

    private String mapSqlError(SQLException e) {
        String sqlState = e.getSQLState();

        if ("23505".equals(sqlState)) {
            return "Registration failed. Email already exists.";
        }

        if ("23514".equals(sqlState)) {
            return "Registration failed. Role must be Player or Coach.";
        }

        if ("42P01".equals(sqlState)) {
            return "Registration failed. Database tables are missing. Run backend/src/main/resources/db/schema.sql.";
        }

        if ("3D000".equals(sqlState)) {
            return "Registration failed. Database does not exist. Check your DB_URL setting.";
        }

        if ("28P01".equals(sqlState)) {
            return "Registration failed. Database login failed. Check DB_USER and DB_PASSWORD.";
        }

        return "Registration failed. Database error: " + e.getMessage();
    }

    @Override
    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("email"),
                            rs.getString("password_hash"),
                            rs.getString("role"),
                            rs.getString("full_name")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
