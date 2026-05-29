package taxsystem.ui.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taxsystem.domain.ChildBenefit;
import taxsystem.domain.EmploymentIncome;
import taxsystem.repository.FileDataRepository;
import taxsystem.service.PersonService;
import taxsystem.service.TaxCalculatorService;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CalculateTaxesCommandTest {

    private PersonService personService;
    private TaxCalculatorService taxService;
    private CalculateTaxesCommand command;
    private ByteArrayOutputStream out;

    @BeforeEach
    void setup() {
        personService = new PersonService(new FileDataRepository());
        taxService = new TaxCalculatorService();
        command = new CalculateTaxesCommand(personService, taxService);
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
    }

    @Test
    void testNoPerson() {
        command.execute(List.of());
        assertTrue(out.toString().contains("Спочатку"));
    }

    @Test
    void testCalculateWithValidData() {
        personService.createPerson("Ivan", "Ivanov", "12345");
        personService.addIncome(new EmploymentIncome("I1", 5000, "job", "Company", true));
        command.execute(List.of());
        assertTrue(out.toString().contains("Податок до пільг"));
        assertTrue(out.toString().contains("Податок після пільг"));
    }

    @Test
    void testCalculateWithBenefits() {
        personService.createPerson("Ivan", "Ivanov", "12345");
        personService.addIncome(new EmploymentIncome("I1", 5000, "job", "Company", true));
        personService.addBenefit(new ChildBenefit("B1", 1000, "child", 2));
        command.execute(List.of());
        String output = out.toString();
        assertTrue(output.contains("після пільг"));
    }

    @Test
    void testCalculateInvalidData() {
        personService.createPerson("Ivan", "Ivanov", "12345");
        personService.getCurrentPerson().getIncomeSources()
                .add(new EmploymentIncome("I1", -100, "job", "Company", true));
        command.execute(List.of());
        assertTrue(out.toString().contains("некоректні"));
    }

    @Test
    void testGetDescription() {
        assertNotNull(command.getDescription());
    }
}
