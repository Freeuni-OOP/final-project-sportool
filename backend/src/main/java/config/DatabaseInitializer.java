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
        } catch (SQLException e) {
            System.err.println("SporTool database initialization skipped: " + e.getMessage());
        }
    }

    private boolean tableExists(Connection conn, String tableName) throws SQLException {
        try (ResultSet rs = conn.getMetaData().getTables(null, "public", tableName, new String[]{"TABLE"})) {
            return rs.next();
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
