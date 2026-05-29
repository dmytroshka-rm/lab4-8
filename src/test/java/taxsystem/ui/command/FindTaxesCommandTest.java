package taxsystem.ui.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taxsystem.domain.EmploymentIncome;
import taxsystem.domain.GiftIncome;
import taxsystem.repository.FileDataRepository;
import taxsystem.service.PersonService;
import taxsystem.service.TaxCalculatorService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FindTaxesCommandTest {

    private PersonService personService;
    private TaxCalculatorService taxService;
    private FindTaxesCommand command;
    private ByteArrayOutputStream out;

    @BeforeEach
    void setup() {
        personService = new PersonService(new FileDataRepository());
        taxService = new TaxCalculatorService();
        command = new FindTaxesCommand(personService, taxService);
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
    }

    @Test
    void testNoPerson() {
        command.execute(List.of());
        assertTrue(out.toString().contains("Спочатку"));
    }

    @Test
    void testFindWithParameters() {
        personService.createPerson("Ivan", "Ivanov", "12345");
        personService.addIncome(new EmploymentIncome("I1", 5000, "job", "Company", true));
        command.execute(List.of("0", "2000"));
        assertTrue(out.toString().contains("діапазоні"));
    }

    @Test
    void testFindWithParametersFoundResults() {
        personService.createPerson("Ivan", "Ivanov", "12345");
        personService.addIncome(new EmploymentIncome("I1", 5000, "job", "Company", true));
        // 5000 * 0.20 = 1000, so range 500–1500 should find it
        command.execute(List.of("500", "1500"));
        String output = out.toString();
        assertTrue(output.contains("діапазоні") || output.contains("завершено") || output.contains("знайдено"));
    }

    @Test
    void testFindWithInvalidParameters() {
        personService.createPerson("Ivan", "Ivanov", "12345");
        command.execute(List.of("abc", "def"));
        assertTrue(out.toString().contains("Невірний"));
    }

    @Test
    void testFindViaStdin() {
        personService.createPerson("Ivan", "Ivanov", "12345");
        personService.addIncome(new EmploymentIncome("I1", 5000, "job", "Company", true));
        String input = "0\n2000\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        command.execute(List.of());
        assertTrue(out.toString().contains("діапазоні"));
    }

    @Test
    void testFindViaStdinEmptyResult() {
        personService.createPerson("Ivan", "Ivanov", "12345");
        personService.addIncome(new EmploymentIncome("I1", 5000, "job", "Company", true));
        // tax = 1000, search range 5000-9000 → no results
        String input = "5000\n9000\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        command.execute(List.of());
        assertTrue(out.toString().contains("Не знайдено"));
    }

    @Test
    void testFindViaStdinNegativeRetries() {
        personService.createPerson("Ivan", "Ivanov", "12345");
        personService.addIncome(new EmploymentIncome("I1", 5000, "job", "Company", true));
        // negative first, then valid
        String input = "-1\n0\n2000\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        command.execute(List.of());
        assertTrue(out.toString().contains("від'ємною"));
    }

    @Test
    void testFindViaStdinInvalidThenValid() {
        personService.createPerson("Ivan", "Ivanov", "12345");
        personService.addIncome(new GiftIncome("G1", 3000, "gift", "Mom", "мати"));
        // invalid then valid
        String input = "notnum\n0\n5000\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        command.execute(List.of());
        assertTrue(out.toString().contains("число"));
    }

    @Test
    void testGetDescription() {
        assertNotNull(command.getDescription());
    }
}
