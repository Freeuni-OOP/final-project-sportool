package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL Driver not found!", e);
        }

        String envUrl = System.getenv("DB_URL");
        String envUser = System.getenv("DB_USER");
        String envPassword = System.getenv("DB_PASSWORD");

        URL = (envUrl != null) ? envUrl : "jdbc:postgresql://localhost:5432/postgres";
        USER = (envUser != null) ? envUser : "postgres";
        PASSWORD = (envPassword != null) ? envPassword : "okmela";
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}