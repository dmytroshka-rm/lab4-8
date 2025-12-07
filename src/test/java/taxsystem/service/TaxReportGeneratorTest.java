package taxsystem.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taxsystem.domain.*;

import static org.junit.jupiter.api.Assertions.*;

class TaxReportGeneratorTest {

    private Person person;
    private TaxReportGenerator generator;

    @BeforeEach
    void setup() {
        person = new Person("P1", "Ivan", "Ivanov", "123");
        person.getIncomeSources().add(
                new EmploymentIncome("I1", 5000, "job", "Company", true)
        );
        person.getTaxBenefits().add(
                new ChildBenefit("B1", 1000, "child", 2)
        );
        generator = new TaxReportGenerator();
    }

    @Test
    void testGenerateDetailedReportWithData() {
        String report = generator.generateDetailedReport(person);

        assertTrue(report.contains("Податковий звіт для Ivan Ivanov"));
        assertTrue(report.contains("EmploymentIncome"));
        assertTrue(report.contains("ChildBenefit"));
    }

    @Test
    void testGenerateDetailedReportWithNullPerson() {
        String report = generator.generateDetailedReport(null);
        assertEquals("Немає даних про особу.", report);
    }

    @Test
    void testGenerateSummaryReportWithData() {
        String report = generator.generateSummaryReport(person);

        assertTrue(report.contains("Платник: Ivan Ivanov"));
        assertTrue(report.contains("Доходів: 1"));
    }

    @Test
    void testGenerateSummaryReportWithNullPerson() {
        String report = generator.generateSummaryReport(null);
        assertEquals("Немає даних про особу.", report);
    }

    @Test
    void testModeSwitchAffectsGenerateReport() {
        String detailed = generator.generateReport(person);
        assertTrue(detailed.contains("Податковий звіт"));

        generator.setSummaryMode();
        String summary = generator.generateReport(person);
        assertTrue(summary.contains("Платник:"));
        assertEquals("SUMMARY", generator.getReportFormat());
        assertFalse(generator.isIncludeDetails());

        generator.setDetailedMode();
        assertEquals("DETAILED", generator.getReportFormat());
        assertTrue(generator.isIncludeDetails());
    }
}
