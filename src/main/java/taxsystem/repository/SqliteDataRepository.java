package taxsystem.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import taxsystem.domain.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqliteDataRepository implements DataRepository {

    private static final Logger log = LogManager.getLogger(SqliteDataRepository.class);

    private final DatabaseManager dbManager;

    public SqliteDataRepository(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    public void save(Person person) {
        if (person == null) {
            log.warn("Спроба зберегти null особу.");
            return;
        }

        String sql = "INSERT OR REPLACE INTO persons (person_id, first_name, last_name, tax_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, person.getPersonId());
            ps.setString(2, person.getFirstName());
            ps.setString(3, person.getLastName());
            ps.setString(4, person.getTaxId());
            ps.executeUpdate();

            saveIncomeSources(conn, person);
            saveTaxBenefits(conn, person);

            log.info("Збережено особу в БД: {} {} (ID: {})",
                    person.getFirstName(), person.getLastName(), person.getPersonId());
        } catch (SQLException e) {
            log.error("Помилка збереження особи в БД: {}", person.getPersonId(), e);
        }
    }

    @Override
    public Optional<Person> findById(String personId) {
        String sql = "SELECT * FROM persons WHERE person_id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, personId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Person person = mapPerson(rs);
                person.setIncomeSources(loadIncomeSources(conn, personId));
                person.setTaxBenefits(loadTaxBenefits(conn, personId));
                return Optional.of(person);
            }
        } catch (SQLException e) {
            log.error("Помилка пошуку особи за ID: {}", personId, e);
        }

        return Optional.empty();
    }

    @Override
    public List<Person> findAll() {
        List<Person> persons = new ArrayList<>();
        String sql = "SELECT * FROM persons";

        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Person person = mapPerson(rs);
                String personId = person.getPersonId();
                person.setIncomeSources(loadIncomeSources(conn, personId));
                person.setTaxBenefits(loadTaxBenefits(conn, personId));
                persons.add(person);
            }
        } catch (SQLException e) {
            log.error("Помилка завантаження списку осіб з БД", e);
        }

        return persons;
    }

    @Override
    public void update(Person person) {
        if (person == null) {
            log.warn("Спроба оновити null особу.");
            return;
        }

        String checkSql = "SELECT COUNT(*) FROM persons WHERE person_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement checkPs = conn.prepareStatement(checkSql)) {

            checkPs.setString(1, person.getPersonId());
            ResultSet rs = checkPs.executeQuery();
            if (!rs.next() || rs.getInt(1) == 0) {
                log.warn("Спроба оновити неіснуючу особу з ID: {}", person.getPersonId());
                return;
            }
        } catch (SQLException e) {
            log.error("Помилка перевірки існування особи: {}", person.getPersonId(), e);
            return;
        }

        save(person);
        log.info("Оновлено особу в БД: {} (ID: {})", person.getLastName(), person.getPersonId());
    }

    @Override
    public void delete(String personId) {
        String sql = "DELETE FROM persons WHERE person_id = ?";

        try (Connection conn = dbManager.getConnection()) {
            deleteIncomeSources(conn, personId);
            deleteTaxBenefits(conn, personId);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, personId);
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    log.info("Видалено особу з БД (ID: {})", personId);
                } else {
                    log.warn("Спроба видалити неіснуючу особу з ID: {}", personId);
                }
            }
        } catch (SQLException e) {
            log.error("Помилка видалення особи з БД: {}", personId, e);
        }
    }

    @Override
    public void exportTaxReport(String reportText, String filename) {
        if (reportText == null || reportText.isBlank()) {
            log.warn("Спроба зберегти порожній звіт.");
            return;
        }

        if (filename == null || filename.isBlank()) {
            filename = "report";
        }
        if (!filename.endsWith(".txt")) {
            filename += ".txt";
        }

        File dir = new File("data");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dir, filename);
        log.info("Експорт звіту у файл '{}'", file.getAbsolutePath());

        try (PrintWriter out = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            out.print(reportText);
            log.info("Звіт успішно експортовано у '{}'", file.getAbsolutePath());
        } catch (IOException e) {
            log.error("Помилка під час експорту звіту у файл '{}'", file.getAbsolutePath(), e);
        }
    }

    // === Приватні методи для роботи з доходами ===

    private void saveIncomeSources(Connection conn, Person person) throws SQLException {
        deleteIncomeSources(conn, person.getPersonId());

        String sql = "INSERT INTO income_sources "
                + "(source_id, person_id, income_type, amount, description, "
                + "employer_name, is_main_job, donor_name, relationship, aid_type, is_taxable, "
                + "work_title, work_type, property_type, is_first_sale, country, currency) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (IncomeSource income : person.getIncomeSources()) {
                ps.setString(1, income.getSourceId());
                ps.setString(2, person.getPersonId());
                ps.setString(3, income.getIncomeType());
                ps.setDouble(4, income.getAmount());
                ps.setString(5, income.getDescription());

                // Обнулити всі типоспецифічні поля
                for (int i = 6; i <= 17; i++) {
                    ps.setNull(i, Types.VARCHAR);
                }

                if (income instanceof EmploymentIncome emp) {
                    ps.setString(6, emp.getEmployerName());
                    ps.setInt(7, emp.isMainJob() ? 1 : 0);
                } else if (income instanceof GiftIncome gift) {
                    ps.setString(8, gift.getDonorName());
                    ps.setString(9, gift.getRelationship());
                } else if (income instanceof MaterialAid aid) {
                    ps.setString(10, aid.getAidType());
                    ps.setInt(11, aid.isTaxable() ? 1 : 0);
                } else if (income instanceof RoyaltyIncome royalty) {
                    ps.setString(12, royalty.getWorkTitle());
                    ps.setString(13, royalty.getWorkType());
                } else if (income instanceof PropertySaleIncome sale) {
                    ps.setString(14, sale.getPropertyType());
                    ps.setInt(15, sale.isFirstSalePerYear() ? 1 : 0);
                } else if (income instanceof ForeignTransferIncome transfer) {
                    ps.setString(16, transfer.getCountry());
                    ps.setString(17, transfer.getCurrency());
                }

                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private List<IncomeSource> loadIncomeSources(Connection conn, String personId) throws SQLException {
        List<IncomeSource> sources = new ArrayList<>();
        String sql = "SELECT * FROM income_sources WHERE person_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, personId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String type = rs.getString("income_type");
                String sourceId = rs.getString("source_id");
                double amount = rs.getDouble("amount");
                String description = rs.getString("description");

                IncomeSource income = switch (type) {
                    case "ОПЛАТА_ПРАЦІ" -> new EmploymentIncome(
                            sourceId, amount, description,
                            rs.getString("employer_name"),
                            rs.getInt("is_main_job") == 1
                    );
                    case "ПОДАРУНОК" -> new GiftIncome(
                            sourceId, amount, description,
                            rs.getString("donor_name"),
                            rs.getString("relationship")
                    );
                    case "МАТЕРІАЛЬНА_ДОПОМОГА" -> new MaterialAid(
                            sourceId, amount, description,
                            rs.getString("aid_type"),
                            rs.getInt("is_taxable") == 1
                    );
                    case "АВТОРСЬКА_ВИНАГОРОДА" -> new RoyaltyIncome(
                            sourceId, amount, description,
                            rs.getString("work_title"),
                            rs.getString("work_type")
                    );
                    case "ПРОДАЖ_МАЙНА" -> new PropertySaleIncome(
                            sourceId, amount, description,
                            rs.getString("property_type"),
                            rs.getInt("is_first_sale") == 1
                    );
                    case "ПЕРЕКАЗ_З_ЗАКОРДОНУ" -> new ForeignTransferIncome(
                            sourceId, amount, description,
                            rs.getString("country"),
                            rs.getString("currency")
                    );
                    default -> {
                        log.warn("Невідомий тип доходу в БД: {}", type);
                        yield null;
                    }
                };

                if (income != null) {
                    sources.add(income);
                }
            }
        }

        return sources;
    }

    private void deleteIncomeSources(Connection conn, String personId) throws SQLException {
        String sql = "DELETE FROM income_sources WHERE person_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, personId);
            ps.executeUpdate();
        }
    }

    // === Приватні методи для роботи з пільгами ===

    private void saveTaxBenefits(Connection conn, Person person) throws SQLException {
        deleteTaxBenefits(conn, person.getPersonId());

        String sql = "INSERT INTO tax_benefits "
                + "(benefit_id, person_id, benefit_type, amount, description, active, child_count, max_non_taxable) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (TaxBenefit benefit : person.getTaxBenefits()) {
                ps.setString(1, benefit.getBenefitId());
                ps.setString(2, person.getPersonId());
                ps.setDouble(4, benefit.getAmount());
                ps.setString(5, benefit.getDescription());
                ps.setInt(6, benefit.isActive() ? 1 : 0);
                ps.setNull(7, Types.INTEGER);
                ps.setNull(8, Types.REAL);

                if (benefit instanceof ChildBenefit child) {
                    ps.setString(3, "CHILD");
                    ps.setInt(7, child.getChildCount());
                } else if (benefit instanceof MaterialAidBenefit mab) {
                    ps.setString(3, "MATERIAL_AID");
                    ps.setDouble(8, mab.getMaxNonTaxableAmount());
                } else {
                    ps.setString(3, "UNKNOWN");
                }

                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private List<TaxBenefit> loadTaxBenefits(Connection conn, String personId) throws SQLException {
        List<TaxBenefit> benefits = new ArrayList<>();
        String sql = "SELECT * FROM tax_benefits WHERE person_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, personId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String type = rs.getString("benefit_type");
                String benefitId = rs.getString("benefit_id");
                double amount = rs.getDouble("amount");
                String description = rs.getString("description");
                boolean active = rs.getInt("active") == 1;

                TaxBenefit benefit = null;

                if ("CHILD".equals(type)) {
                    int childCount = rs.getInt("child_count");
                    benefit = new ChildBenefit(benefitId, amount, description, childCount);
                } else if ("MATERIAL_AID".equals(type)) {
                    double maxNonTaxable = rs.getDouble("max_non_taxable");
                    benefit = new MaterialAidBenefit(benefitId, amount, description, maxNonTaxable);
                }

                if (benefit != null) {
                    benefit.setActive(active);
                    benefits.add(benefit);
                }
            }
        }

        return benefits;
    }

    private void deleteTaxBenefits(Connection conn, String personId) throws SQLException {
        String sql = "DELETE FROM tax_benefits WHERE person_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, personId);
            ps.executeUpdate();
        }
    }

    // === Маппінг ===

    private Person mapPerson(ResultSet rs) throws SQLException {
        return new Person(
                rs.getString("person_id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("tax_id")
        );
    }
}
