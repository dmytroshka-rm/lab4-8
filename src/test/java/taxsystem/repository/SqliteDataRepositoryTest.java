package taxsystem.repository;

import org.junit.jupiter.api.*;
import taxsystem.domain.*;

import java.io.File;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SqliteDataRepositoryTest {

    private static final String TEST_DB = "test_taxsystem.db";
    private SqliteDataRepository repo;

    @BeforeEach
    void setup() {
        new File(TEST_DB).delete();
        DatabaseManager dbManager = new DatabaseManager(TEST_DB);
        repo = new SqliteDataRepository(dbManager);
    }

    @AfterEach
    void cleanup() {
        new File(TEST_DB).delete();
    }

    @Test
    void testSaveAndFindById() {
        Person person = new Person("P1", "Ivan", "Ivanov", "12345");
        repo.save(person);

        Optional<Person> found = repo.findById("P1");
        assertTrue(found.isPresent());
        assertEquals("Ivan", found.get().getFirstName());
        assertEquals("Ivanov", found.get().getLastName());
        assertEquals("12345", found.get().getTaxId());
    }

    @Test
    void testFindByIdNotFound() {
        assertFalse(repo.findById("NONE").isPresent());
    }

    @Test
    void testFindAll() {
        repo.save(new Person("P1", "Ivan", "Ivanov", "111"));
        repo.save(new Person("P2", "Petro", "Petrov", "222"));

        List<Person> all = repo.findAll();
        assertEquals(2, all.size());
    }

    @Test
    void testSaveNullIgnored() {
        repo.save(null);
        assertTrue(repo.findAll().isEmpty());
    }

    @Test
    void testDelete() {
        repo.save(new Person("P1", "Ivan", "Ivanov", "111"));
        repo.delete("P1");

        assertFalse(repo.findById("P1").isPresent());
    }

    @Test
    void testDeleteNonExistent() {
        repo.delete("NONE");
        assertTrue(repo.findAll().isEmpty());
    }

    @Test
    void testUpdateExistingPerson() {
        Person person = new Person("P1", "Ivan", "Ivanov", "12345");
        repo.save(person);

        person.setFirstName("Petro");
        repo.update(person);

        Optional<Person> found = repo.findById("P1");
        assertTrue(found.isPresent());
        assertEquals("Petro", found.get().getFirstName());
    }

    @Test
    void testUpdateNonExistentIgnored() {
        Person person = new Person("NONE", "Ivan", "Ivanov", "12345");
        repo.update(person);

        assertFalse(repo.findById("NONE").isPresent());
    }

    @Test
    void testSaveWithEmploymentIncome() {
        Person person = new Person("P1", "Ivan", "Ivanov", "12345");
        person.getIncomeSources().add(
                new EmploymentIncome("I1", 5000, "Зарплата", "Google", true)
        );
        repo.save(person);

        Optional<Person> found = repo.findById("P1");
        assertTrue(found.isPresent());
        assertEquals(1, found.get().getIncomeSources().size());

        IncomeSource income = found.get().getIncomeSources().get(0);
        assertInstanceOf(EmploymentIncome.class, income);

        EmploymentIncome emp = (EmploymentIncome) income;
        assertEquals("I1", emp.getSourceId());
        assertEquals(5000, emp.getAmount());
        assertEquals("Зарплата", emp.getDescription());
        assertEquals("Google", emp.getEmployerName());
        assertTrue(emp.isMainJob());
    }

    @Test
    void testSaveWithGiftIncome() {
        Person person = new Person("P1", "Ivan", "Ivanov", "12345");
        person.getIncomeSources().add(
                new GiftIncome("G1", 10000, "Подарунок", "Мама", "мати")
        );
        repo.save(person);

        Optional<Person> found = repo.findById("P1");
        assertTrue(found.isPresent());

        GiftIncome gift = (GiftIncome) found.get().getIncomeSources().get(0);
        assertEquals("Мама", gift.getDonorName());
        assertEquals("мати", gift.getRelationship());
        assertTrue(gift.isCloseRelative());
    }

    @Test
    void testSaveWithMaterialAid() {
        Person person = new Person("P1", "Ivan", "Ivanov", "12345");
        person.getIncomeSources().add(
                new MaterialAid("A1", 3000, "Допомога", "одноразова", true)
        );
        repo.save(person);

        Optional<Person> found = repo.findById("P1");
        assertTrue(found.isPresent());

        MaterialAid aid = (MaterialAid) found.get().getIncomeSources().get(0);
        assertEquals("одноразова", aid.getAidType());
        assertTrue(aid.isTaxable());
    }

    @Test
    void testSaveWithChildBenefit() {
        Person person = new Person("P1", "Ivan", "Ivanov", "12345");
        person.getTaxBenefits().add(
                new ChildBenefit("B1", 1000, "Пільга на дітей", 3)
        );
        repo.save(person);

        Optional<Person> found = repo.findById("P1");
        assertTrue(found.isPresent());
        assertEquals(1, found.get().getTaxBenefits().size());

        ChildBenefit benefit = (ChildBenefit) found.get().getTaxBenefits().get(0);
        assertEquals("B1", benefit.getBenefitId());
        assertEquals(1000, benefit.getAmount());
        assertEquals(3, benefit.getChildCount());
        assertTrue(benefit.isActive());
    }

    @Test
    void testSaveWithMultipleIncomesAndBenefits() {
        Person person = new Person("P1", "Ivan", "Ivanov", "12345");
        person.getIncomeSources().add(new EmploymentIncome("I1", 5000, "Зарплата", "Company", true));
        person.getIncomeSources().add(new GiftIncome("G1", 2000, "Подарунок", "Друг", "друг"));
        person.getIncomeSources().add(new MaterialAid("A1", 1000, "Допомога", "соціальна", false));
        person.getTaxBenefits().add(new ChildBenefit("B1", 500, "Діти", 2));

        repo.save(person);

        Optional<Person> found = repo.findById("P1");
        assertTrue(found.isPresent());
        assertEquals(3, found.get().getIncomeSources().size());
        assertEquals(1, found.get().getTaxBenefits().size());
    }

    @Test
    void testDeleteCascadesIncomesAndBenefits() {
        Person person = new Person("P1", "Ivan", "Ivanov", "12345");
        person.getIncomeSources().add(new EmploymentIncome("I1", 5000, "Зарплата", "Company", true));
        person.getTaxBenefits().add(new ChildBenefit("B1", 500, "Діти", 2));
        repo.save(person);

        repo.delete("P1");

        assertFalse(repo.findById("P1").isPresent());
    }

    @Test
    void testExportCreatesFile() {
        repo.exportTaxReport("hello world", "test_export");

        File file = new File("data/test_export.txt");
        assertTrue(file.exists());
        file.delete();
    }

    @Test
    void testExportEmptyReportIgnored() {
        repo.exportTaxReport("", "empty");
        assertFalse(new File("data/empty.txt").exists());
    }

    @Test
    void testInactiveBenefitPreserved() {
        Person person = new Person("P1", "Ivan", "Ivanov", "12345");
        ChildBenefit benefit = new ChildBenefit("B1", 1000, "Пільга", 2);
        benefit.setActive(false);
        person.getTaxBenefits().add(benefit);
        repo.save(person);

        Optional<Person> found = repo.findById("P1");
        assertTrue(found.isPresent());
        assertFalse(found.get().getTaxBenefits().get(0).isActive());
    }

    @Test
    void testUpdateNullIgnored() {
        repo.update(null);
        assertTrue(repo.findAll().isEmpty());
    }

    @Test
    void testSaveWithRoyaltyIncome() {
        Person person = new Person("P1", "Ivan", "Ivanov", "12345");
        person.getIncomeSources().add(
                new RoyaltyIncome("R1", 8000, "Книга", "Моя книга", "книга")
        );
        repo.save(person);

        Optional<Person> found = repo.findById("P1");
        assertTrue(found.isPresent());
        assertEquals(1, found.get().getIncomeSources().size());

        RoyaltyIncome royalty = (RoyaltyIncome) found.get().getIncomeSources().get(0);
        assertEquals("R1", royalty.getSourceId());
        assertEquals(8000, royalty.getAmount());
        assertEquals("Моя книга", royalty.getWorkTitle());
        assertEquals("книга", royalty.getWorkType());
    }

    @Test
    void testSaveWithPropertySaleIncome() {
        Person person = new Person("P1", "Ivan", "Ivanov", "12345");
        person.getIncomeSources().add(
                new PropertySaleIncome("S1", 500000, "Продаж квартири", "квартира", true)
        );
        repo.save(person);

        Optional<Person> found = repo.findById("P1");
        assertTrue(found.isPresent());

        PropertySaleIncome sale = (PropertySaleIncome) found.get().getIncomeSources().get(0);
        assertEquals("S1", sale.getSourceId());
        assertEquals(500000, sale.getAmount());
        assertEquals("квартира", sale.getPropertyType());
        assertTrue(sale.isFirstSalePerYear());
    }

    @Test
    void testSaveWithPropertySaleNotFirstSale() {
        Person person = new Person("P1", "Ivan", "Ivanov", "12345");
        person.getIncomeSources().add(
                new PropertySaleIncome("S2", 300000, "Продаж авто", "авто", false)
        );
        repo.save(person);

        Optional<Person> found = repo.findById("P1");
        assertTrue(found.isPresent());

        PropertySaleIncome sale = (PropertySaleIncome) found.get().getIncomeSources().get(0);
        assertFalse(sale.isFirstSalePerYear());
    }

    @Test
    void testSaveWithForeignTransferIncome() {
        Person person = new Person("P1", "Ivan", "Ivanov", "12345");
        person.getIncomeSources().add(
                new ForeignTransferIncome("F1", 25000, "Переказ", "Польща", "EUR")
        );
        repo.save(person);

        Optional<Person> found = repo.findById("P1");
        assertTrue(found.isPresent());

        ForeignTransferIncome transfer = (ForeignTransferIncome) found.get().getIncomeSources().get(0);
        assertEquals("F1", transfer.getSourceId());
        assertEquals(25000, transfer.getAmount());
        assertEquals("Польща", transfer.getCountry());
        assertEquals("EUR", transfer.getCurrency());
    }

    @Test
    void testSaveWithMaterialAidBenefit() {
        Person person = new Person("P1", "Ivan", "Ivanov", "12345");
        person.getTaxBenefits().add(
                new MaterialAidBenefit("MB1", 5000, "Пільга на допомогу", 3000)
        );
        repo.save(person);

        Optional<Person> found = repo.findById("P1");
        assertTrue(found.isPresent());
        assertEquals(1, found.get().getTaxBenefits().size());

        MaterialAidBenefit mab = (MaterialAidBenefit) found.get().getTaxBenefits().get(0);
        assertEquals("MB1", mab.getBenefitId());
        assertEquals(5000, mab.getAmount());
        assertEquals(3000, mab.getMaxNonTaxableAmount());
        assertTrue(mab.isActive());
    }

    @Test
    void testSaveWithAllIncomeTypes() {
        Person person = new Person("P1", "Ivan", "Ivanov", "12345");
        person.getIncomeSources().add(new EmploymentIncome("I1", 5000, "Зарплата", "Company", true));
        person.getIncomeSources().add(new GiftIncome("G1", 2000, "Подарунок", "Друг", "друг"));
        person.getIncomeSources().add(new MaterialAid("A1", 1000, "Допомога", "соціальна", false));
        person.getIncomeSources().add(new RoyaltyIncome("R1", 8000, "Книга", "Моя книга", "книга"));
        person.getIncomeSources().add(new PropertySaleIncome("S1", 500000, "Продаж", "квартира", true));
        person.getIncomeSources().add(new ForeignTransferIncome("F1", 25000, "Переказ", "Німеччина", "USD"));
        repo.save(person);

        Optional<Person> found = repo.findById("P1");
        assertTrue(found.isPresent());
        assertEquals(6, found.get().getIncomeSources().size());
    }

    @Test
    void testExportWithNullFilename() {
        repo.exportTaxReport("report content", null);
        File file = new File("data/report.txt");
        assertTrue(file.exists());
        file.delete();
    }

    @Test
    void testExportWithBlankFilename() {
        repo.exportTaxReport("report content", "   ");
        File file = new File("data/report.txt");
        assertTrue(file.exists());
        file.delete();
    }

    @Test
    void testExportNullReportIgnored() {
        repo.exportTaxReport(null, "test");
        assertFalse(new File("data/test.txt").exists());
    }

    @Test
    void testExportWithTxtExtensionNotDuplicated() {
        repo.exportTaxReport("data", "myreport.txt");
        File file = new File("data/myreport.txt");
        assertTrue(file.exists());
        file.delete();
    }

    @Test
    void testSaveOverwritesExistingPerson() {
        Person person = new Person("P1", "Ivan", "Ivanov", "12345");
        person.getIncomeSources().add(new EmploymentIncome("I1", 5000, "Зарплата", "Company", true));
        repo.save(person);

        Person updated = new Person("P1", "Petro", "Petrov", "54321");
        updated.getIncomeSources().add(new GiftIncome("G1", 2000, "Подарунок", "Мама", "мати"));
        repo.save(updated);

        Optional<Person> found = repo.findById("P1");
        assertTrue(found.isPresent());
        assertEquals("Petro", found.get().getFirstName());
        assertEquals(1, found.get().getIncomeSources().size());
        assertInstanceOf(GiftIncome.class, found.get().getIncomeSources().get(0));
    }

    @Test
    void testSaveWithMixedBenefits() {
        Person person = new Person("P1", "Ivan", "Ivanov", "12345");
        person.getTaxBenefits().add(new ChildBenefit("B1", 1000, "Діти", 2));
        person.getTaxBenefits().add(new MaterialAidBenefit("MB1", 5000, "Допомога", 3000));
        repo.save(person);

        Optional<Person> found = repo.findById("P1");
        assertTrue(found.isPresent());
        assertEquals(2, found.get().getTaxBenefits().size());
    }
}
