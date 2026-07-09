package config;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.stream.Collectors;

@WebListener
public class DatabaseInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try (Connection conn = DBConnection.getConnection()) {
            if (isSchemaInitializationNeeded(conn)) {
                runSchemaScript(conn);
                System.out.println("SporTool database schema initialized.");
            }

            seedCourtsIfEmpty(conn);
            ensureBookingPaymentColumns(conn);
            ensureCoachTables(conn);
            seedAdditionalCourts(conn);
        } catch (SQLException e) {
            System.err.println("SporTool database initialization skipped: " + e.getMessage());
        }
    }

    private boolean isSchemaInitializationNeeded(Connection conn) throws SQLException {
        return !tableExists(conn, "users")
                || !tableExists(conn, "courts")
                || !tableExists(conn, "trainers")
                || !tableExists(conn, "bookings")
                || !tableExists(conn, "posts")
                || !tableExists(conn, "comments")
                || !tableExists(conn, "match_announcements")
                || !tableExists(conn, "match_joins");
    }

    private boolean tableExists(Connection conn, String tableName) throws SQLException {
        try (ResultSet rs = conn.getMetaData().getTables(null, "public", tableName, new String[]{"TABLE"})) {
            return rs.next();
        }
    }

    private void seedCourtsIfEmpty(Connection conn) throws SQLException {
        if (!tableExists(conn, "courts")) {
            return;
        }

        try (Statement check = conn.createStatement();
             ResultSet rs = check.executeQuery("SELECT COUNT(*) FROM courts")) {
            if (rs.next() && rs.getInt(1) > 0) {
                return;
            }
        }

        String insertCourts = """
                INSERT INTO courts (id, court_name, court_type, location, price_per_hour) VALUES
                (1, 'Padel Tbilisi', 'Padel', 'Vake Park', 55.00),
                (2, 'Saburtalo Football Arena', 'Football', 'Tsintsadze St', 40.00),
                (3, 'Marjanishvili Tennis Club', 'Tennis', 'Marjanishvili', 35.00),
                (4, 'Vera Park Basketball Court', 'Basketball', 'Vera', 25.00),
                (5, 'Digomi Padel Center', 'Padel', 'Digomi', 50.00),
                (6, 'Lisi Lake Football Pitch', 'Football', 'Lisi', 45.00),
                (7, 'Rustaveli Tennis Academy', 'Tennis', 'Rustaveli Ave', 40.00),
                (8, 'Gldani Indoor Basketball', 'Basketball', 'Gldani', 30.00)
                ON CONFLICT (id) DO NOTHING
                """;

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(insertCourts);
            stmt.execute(
                    "SELECT setval(pg_get_serial_sequence('courts', 'id'), " +
                    "(SELECT COALESCE(MAX(id), 1) FROM courts))"
            );
            System.out.println("SporTool courts seeded.");
        }
    }

    private void ensureBookingPaymentColumns(Connection conn) throws SQLException {
        if (!tableExists(conn, "bookings")) {
            return;
        }

        try (Statement stmt = conn.createStatement()) {
            stmt.execute("ALTER TABLE bookings ADD COLUMN IF NOT EXISTS payment_status VARCHAR(20) DEFAULT 'PAID'");
            stmt.execute("ALTER TABLE bookings ADD COLUMN IF NOT EXISTS payment_reference VARCHAR(100)");
        }
    }

    private void ensureCoachTables(Connection conn) throws SQLException {
        if (!tableExists(conn, "trainers")) {
            return;
        }

        try (Statement stmt = conn.createStatement()) {
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS trainer_profiles (
                        trainer_id INT PRIMARY KEY REFERENCES trainers(id) ON DELETE CASCADE,
                        description TEXT,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                    """);
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS trainer_venues (
                        id SERIAL PRIMARY KEY,
                        trainer_id INT NOT NULL REFERENCES trainers(id) ON DELETE CASCADE,
                        venue_name VARCHAR(255) NOT NULL,
                        address VARCHAR(255) NOT NULL,
                        price_override NUMERIC(10, 2)
                    )
                    """);
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS trainer_availability (
                        id SERIAL PRIMARY KEY,
                        trainer_venue_id INT NOT NULL REFERENCES trainer_venues(id) ON DELETE CASCADE,
                        day_of_week VARCHAR(20) NOT NULL,
                        start_time VARCHAR(10) NOT NULL,
                        end_time VARCHAR(10) NOT NULL
                    )
                    """);
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS trainer_bookings (
                        id SERIAL PRIMARY KEY,
                        trainer_id INT NOT NULL REFERENCES trainers(id) ON DELETE CASCADE,
                        trainer_venue_id INT NOT NULL REFERENCES trainer_venues(id) ON DELETE CASCADE,
                        player_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                        requested_date DATE NOT NULL,
                        requested_time_slot VARCHAR(50) NOT NULL,
                        status VARCHAR(20) DEFAULT 'PENDING',
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                    """);
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS trainer_reviews (
                        id SERIAL PRIMARY KEY,
                        trainer_id INT NOT NULL REFERENCES trainers(id) ON DELETE CASCADE,
                        player_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                        rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
                        comment TEXT,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        UNIQUE (trainer_id, player_id)
                    )
                    """);
        }
    }

    private void seedAdditionalCourts(Connection conn) throws SQLException {
        if (!tableExists(conn, "courts")) {
            return;
        }

        String insertAdditionalCourts = """
                INSERT INTO courts (id, court_name, court_type, location, price_per_hour) VALUES
                (5, 'Digomi Padel Center', 'Padel', 'Digomi', 50.00),
                (6, 'Lisi Lake Football Pitch', 'Football', 'Lisi', 45.00),
                (7, 'Rustaveli Tennis Academy', 'Tennis', 'Rustaveli Ave', 40.00),
                (8, 'Gldani Indoor Basketball', 'Basketball', 'Gldani', 30.00)
                ON CONFLICT (id) DO NOTHING
                """;

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(insertAdditionalCourts);
            stmt.execute(
                    "SELECT setval(pg_get_serial_sequence('courts', 'id'), " +
                    "(SELECT COALESCE(MAX(id), 1) FROM courts))"
            );
        }
    }

    private void runSchemaScript(Connection conn) throws SQLException {
        InputStream stream = DatabaseInitializer.class.getClassLoader()
                .getResourceAsStream("db/schema.sql");

        if (stream == null) {
            throw new SQLException("schema.sql not found on classpath");
        }

        String sql = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));

        for (String statement : sql.split(";")) {
            String cleaned = Arrays.stream(statement.split("\n"))
                    .map(String::trim)
                    .filter(line -> !line.isEmpty() && !line.startsWith("--"))
                    .collect(Collectors.joining("\n"))
                    .trim();

            if (cleaned.isEmpty()) {
                continue;
            }

            try (Statement stmt = conn.createStatement()) {
                stmt.execute(cleaned);
            }
        }
    }
}
