package taxsystem.ui.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taxsystem.domain.EmploymentIncome;
import taxsystem.domain.GiftIncome;
import taxsystem.repository.FileDataRepository;
import taxsystem.service.PersonService;
import taxsystem.service.TaxCalculatorService;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SortTaxesCommandTest {

    private PersonService personService;
    private TaxCalculatorService taxService;
    private SortTaxesCommand command;
    private ByteArrayOutputStream out;

    @BeforeEach
    void setup() {
        personService = new PersonService(new FileDataRepository());
        taxService = new TaxCalculatorService();
        command = new SortTaxesCommand(personService, taxService);
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
    }

    @Test
    void testNoPerson() {
        command.execute(List.of());
        assertTrue(out.toString().contains("Спочатку"));
    }

    @Test
    void testSortAscending() {
        personService.createPerson("Ivan", "Ivanov", "12345");
        personService.addIncome(new EmploymentIncome("I1", 5000, "big", "Comp", true));
        personService.addIncome(new GiftIncome("G1", 1000, "small gift", "Friend", "друг"));
        command.execute(List.of("asc"));
        assertTrue(out.toString().contains("зростання"));
    }

    @Test
    void testSortDescending() {
        personService.createPerson("Ivan", "Ivanov", "12345");
        personService.addIncome(new EmploymentIncome("I1", 5000, "big", "Comp", true));
        personService.addIncome(new GiftIncome("G1", 1000, "small gift", "Friend", "друг"));
        command.execute(List.of("desc"));
        assertTrue(out.toString().contains("спадання"));
    }

    @Test
    void testSortDefaultAscending() {
        personService.createPerson("Ivan", "Ivanov", "12345");
        personService.addIncome(new EmploymentIncome("I1", 5000, "big", "Comp", true));
        command.execute(List.of());
        assertTrue(out.toString().contains("зростання"));
    }

    @Test
    void testSortEmptyIncomes() {
        personService.createPerson("Ivan", "Ivanov", "12345");
        command.execute(List.of());
        assertTrue(out.toString().contains("Немає"));
    }

    @Test
    void testGetDescription() {
        assertNotNull(command.getDescription());
    }
}
