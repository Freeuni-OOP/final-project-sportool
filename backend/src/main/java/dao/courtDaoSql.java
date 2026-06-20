package dao;

import config.DBConnection;
import model.Court;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class courtDaoSql implements courtDao{
    @Override
    public List<Court> getCourtsByType(String type) {
        List<Court> courts = new ArrayList<>();
        String query = "SELECT * FROM courts WHERE court_type = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, type.toUpperCase());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    courts.add(new Court(
                            rs.getInt("id"),
                            rs.getString("court_name"),
                            rs.getString("court_type"),
                            rs.getString("location"),
                            rs.getDouble("price_per_hour"))
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courts;
    }

    @Override
    public List<Court> getAllCourts() {
        List<Court> courts = new ArrayList<>();
        String query = "SELECT id, court_name, court_type, location, price_per_hour FROM courts";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                courts.add(new Court(
                        rs.getInt("id"),
                        rs.getString("court_name"),
                        rs.getString("court_type"),
                        rs.getString("location"),
                        rs.getDouble("price_per_hour")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courts;
    }
}
