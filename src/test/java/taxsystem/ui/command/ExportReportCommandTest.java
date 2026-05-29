package taxsystem.ui.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taxsystem.domain.EmploymentIncome;
import taxsystem.repository.FileDataRepository;
import taxsystem.service.PersonService;
import taxsystem.service.TaxCalculatorService;
import taxsystem.service.TaxReportGenerator;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExportReportCommandTest {

    private PersonService personService;
    private TaxCalculatorService taxService;
    private TaxReportGenerator reportGenerator;
    private FileDataRepository repository;
    private ExportReportCommand command;
    private ByteArrayOutputStream out;

    @BeforeEach
    void setup() {
        repository = new FileDataRepository();
        personService = new PersonService(repository);
        taxService = new TaxCalculatorService();
        reportGenerator = new TaxReportGenerator();
        command = new ExportReportCommand(personService, taxService, reportGenerator, repository);
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
    }

    @Test
    void testNoFilename() {
        command.execute(List.of());
        assertTrue(out.toString().contains("Використання"));
    }

    @Test
    void testNoPerson() {
        command.execute(List.of("report.txt"));
        assertTrue(out.toString().contains("Спочатку"));
    }

    @Test
    void testExportSuccess() {
        personService.createPerson("Ivan", "Ivanov", "12345");
        personService.addIncome(new EmploymentIncome("I1", 5000, "job", "Company", true));
        command.execute(List.of("test_report.txt"));
        assertTrue(out.toString().contains("експортовано"));
    }

    @Test
    void testExportWithNoIncomes() {
        personService.createPerson("Ivan", "Ivanov", "12345");
        command.execute(List.of("empty_report.txt"));
        assertTrue(out.toString().contains("експортовано"));
    }

    @Test
    void testGetDescription() {
        assertNotNull(command.getDescription());
    }
}
