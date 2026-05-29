package taxsystem.repository;

import org.junit.jupiter.api.*;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseManagerTest {

    private static final String TEST_DB = "test_dbmanager.db";

    @AfterEach
    void cleanup() {
        new File(TEST_DB).delete();
    }

    @Test
    void testSchemaCreated() throws SQLException {
        DatabaseManager dbManager = new DatabaseManager(TEST_DB);

        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery(
                    "SELECT name FROM sqlite_master WHERE type='table' ORDER BY name"
            );

            boolean hasPersons = false;
            boolean hasIncomeSources = false;
            boolean hasTaxBenefits = false;

            while (rs.next()) {
                String tableName = rs.getString("name");
                if ("persons".equals(tableName)) hasPersons = true;
                if ("income_sources".equals(tableName)) hasIncomeSources = true;
                if ("tax_benefits".equals(tableName)) hasTaxBenefits = true;
            }

            assertTrue(hasPersons, "Таблиця persons має існувати");
            assertTrue(hasIncomeSources, "Таблиця income_sources має існувати");
            assertTrue(hasTaxBenefits, "Таблиця tax_benefits має існувати");
        }
    }

    @Test
    void testGetConnectionWorks() throws SQLException {
        DatabaseManager dbManager = new DatabaseManager(TEST_DB);

        try (Connection conn = dbManager.getConnection()) {
            assertNotNull(conn);
            assertFalse(conn.isClosed());
        }
    }

    @Test
    void testSchemaIdempotent() {
        new DatabaseManager(TEST_DB);
        assertDoesNotThrow(() -> new DatabaseManager(TEST_DB));
    }

    @Test
    void testUsersTableCreated() throws SQLException {
        DatabaseManager dbManager = new DatabaseManager(TEST_DB);

        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery(
                    "SELECT name FROM sqlite_master WHERE type='table' AND name='users'"
            );
            assertTrue(rs.next(), "Таблиця users має існувати");
        }
    }

    @Test
    void testInvalidDbPathThrows() {
        assertThrows(RuntimeException.class, () ->
                new DatabaseManager("/nonexistent/path/that/cannot/exist/db.db")
        );
    }
}
