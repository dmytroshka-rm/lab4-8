package taxsystem.ui.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taxsystem.domain.Person;
import taxsystem.repository.FileDataRepository;
import taxsystem.service.PersonService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CreatePersonCommandTest {

    private PersonService personService;
    private CreatePersonCommand command;
    private ByteArrayOutputStream out;

    @BeforeEach
    void setup() {
        personService = new PersonService(new FileDataRepository());
        command = new CreatePersonCommand(personService);
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
    }

    @Test
    void testCreatePersonSuccess() {
        String input = "Ivan\nIvanov\n1234567890\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        command.execute(List.of());

        assertNotNull(personService.getCurrentPerson());
        assertEquals("Ivan", personService.getCurrentPerson().getFirstName());
        assertEquals("Ivanov", personService.getCurrentPerson().getLastName());
        assertEquals("1234567890", personService.getCurrentPerson().getTaxId());
    }

    @Test
    void testCreatePersonEmptyFieldsRejected() {
        String input = "\n\n\n\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        command.execute(List.of());

        assertNull(personService.getCurrentPerson());
        assertTrue(out.toString().contains("обов'язкові"));
    }

    @Test
    void testGetDescription() {
        assertNotNull(command.getDescription());
        assertFalse(command.getDescription().isEmpty());
    }
}
