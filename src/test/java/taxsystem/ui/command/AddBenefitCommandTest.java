package taxsystem.ui.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taxsystem.repository.FileDataRepository;
import taxsystem.service.PersonService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AddBenefitCommandTest {

    private PersonService personService;
    private AddBenefitCommand command;
    private ByteArrayOutputStream out;

    @BeforeEach
    void setup() {
        personService = new PersonService(new FileDataRepository());
        command = new AddBenefitCommand(personService);
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        personService.createPerson("Ivan", "Ivanov", "12345");
    }

    @Test
    void testAddChildBenefit() {
        String input = "child\nB1\n1000\n2\nПільга на дітей\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        command.execute(List.of());
        assertEquals(1, personService.getCurrentPerson().getTaxBenefits().size());
    }

    @Test
    void testAddBenefitNoPerson() {
        personService = new PersonService(new FileDataRepository());
        command = new AddBenefitCommand(personService);
        command.execute(List.of());
        assertTrue(out.toString().contains("Спочатку"));
    }

    @Test
    void testUnknownTypeRejected() {
        String input = "xyz\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        command.execute(List.of());
        assertTrue(out.toString().contains("Невідома"));
        assertEquals(0, personService.getCurrentPerson().getTaxBenefits().size());
    }

    @Test
    void testInvalidAmountRetries() {
        String input = "child\nB1\nnotanumber\n1000\nnotanumber\n2\nПільга\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        command.execute(List.of());
        assertEquals(1, personService.getCurrentPerson().getTaxBenefits().size());
    }

    @Test
    void testZeroAmountRetries() {
        String input = "child\nB1\n0\n1000\n0\n2\nПільга\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        command.execute(List.of());
        assertEquals(1, personService.getCurrentPerson().getTaxBenefits().size());
    }

    @Test
    void testGetDescription() {
        assertNotNull(command.getDescription());
    }
}
