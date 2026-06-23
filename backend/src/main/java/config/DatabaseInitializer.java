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
            if (!tableExists(conn, "users")) {
                runSchemaScript(conn);
                System.out.println("SporTool database schema initialized.");
            }

            seedCourtsIfEmpty(conn);
        } catch (SQLException e) {
            System.err.println("SporTool database initialization skipped: " + e.getMessage());
        }
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
                (4, 'Vera Park Basketball Court', 'Basketball', 'Vera', 25.00)
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
