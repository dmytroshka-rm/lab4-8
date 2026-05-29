package taxsystem.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final Logger log = LogManager.getLogger(DatabaseManager.class);

    private final String url;

    public DatabaseManager(String dbPath) {
        this.url = "jdbc:sqlite:" + dbPath;
        initializeSchema();
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }

    private void initializeSchema() {
        log.info("Ініціалізація схеми бази даних...");

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS persons ("
                    + "person_id TEXT PRIMARY KEY, "
                    + "first_name TEXT NOT NULL, "
                    + "last_name TEXT NOT NULL, "
                    + "tax_id TEXT NOT NULL"
                    + ")");

            stmt.execute("CREATE TABLE IF NOT EXISTS income_sources ("
                    + "source_id TEXT PRIMARY KEY, "
                    + "person_id TEXT NOT NULL, "
                    + "income_type TEXT NOT NULL, "
                    + "amount REAL NOT NULL, "
                    + "description TEXT, "
                    + "employer_name TEXT, "
                    + "is_main_job INTEGER, "
                    + "donor_name TEXT, "
                    + "relationship TEXT, "
                    + "aid_type TEXT, "
                    + "is_taxable INTEGER, "
                    + "work_title TEXT, "
                    + "work_type TEXT, "
                    + "property_type TEXT, "
                    + "is_first_sale INTEGER, "
                    + "country TEXT, "
                    + "currency TEXT, "
                    + "FOREIGN KEY (person_id) REFERENCES persons(person_id) ON DELETE CASCADE"
                    + ")");

            stmt.execute("CREATE TABLE IF NOT EXISTS tax_benefits ("
                    + "benefit_id TEXT PRIMARY KEY, "
                    + "person_id TEXT NOT NULL, "
                    + "benefit_type TEXT NOT NULL, "
                    + "amount REAL NOT NULL, "
                    + "description TEXT, "
                    + "active INTEGER DEFAULT 1, "
                    + "child_count INTEGER, "
                    + "max_non_taxable REAL, "
                    + "FOREIGN KEY (person_id) REFERENCES persons(person_id) ON DELETE CASCADE"
                    + ")");

            stmt.execute("CREATE TABLE IF NOT EXISTS users ("
                    + "username TEXT PRIMARY KEY, "
                    + "password_hash TEXT NOT NULL, "
                    + "email TEXT"
                    + ")");

            log.info("Схема бази даних успішно створена.");
        } catch (SQLException e) {
            log.error("Помилка ініціалізації схеми бази даних!", e);
            throw new RuntimeException("Не вдалося ініціалізувати базу даних", e);
        }
    }
}
