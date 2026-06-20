package config;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.flywaydb.core.Flyway;

@WebListener
public class DatabaseMigrationListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            // Using standard JDBC credentials aligned with your architecture
            String url = "jdbc:postgresql://localhost:5432/sportool";
            String user = "postgres";
            String password = "password"; // CHANGE THIS to your actual local DB password

            // Configure Flyway to handle migration scripts automatically on startup
            Flyway flyway = Flyway.configure()
                    .dataSource(url, user, password)
                    .baselineOnMigrate(true) // Baselines the existing database from Stage 1
                    .load();

            System.out.println("Executing database migrations via Flyway...");
            flyway.migrate();
            System.out.println("Database migrations completed successfully.");

        } catch (Exception e) {
            System.err.println("Database migration failed during application startup!");
            e.printStackTrace();
            throw new RuntimeException("Could not initialize database schema", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // No teardown logic needed for database migration lifecycle
    }
}