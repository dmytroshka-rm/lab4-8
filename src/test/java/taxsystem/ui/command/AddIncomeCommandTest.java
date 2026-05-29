package taxsystem.ui.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taxsystem.repository.FileDataRepository;
import taxsystem.service.PersonService;
import taxsystem.service.TaxCalculatorService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AddIncomeCommandTest {

    private PersonService personService;
    private TaxCalculatorService taxService;
    private AddIncomeCommand command;
    private ByteArrayOutputStream out;

    @BeforeEach
    void setup() {
        personService = new PersonService(new FileDataRepository());
        taxService = new TaxCalculatorService();
        command = new AddIncomeCommand(personService, taxService);
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        personService.createPerson("Ivan", "Ivanov", "12345");
    }

    @Test
    void testAddIncomeNoPerson() {
        personService = new PersonService(new FileDataRepository());
        command = new AddIncomeCommand(personService, taxService);
        command.execute(List.of());
        assertTrue(out.toString().contains("Спочатку"));
    }

    @Test
    void testAddEmploymentIncome() {
        String input = "employment\nI1\n5000\nGoogle\ntrue\nЗарплата\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        command.execute(List.of());
        assertEquals(1, personService.getCurrentPerson().getIncomeSources().size());
    }

    @Test
    void testAddGiftIncome() {
        String input = "gift\nG1\n10000\nМама\nмати\nПодарунок\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        command.execute(List.of());
        assertEquals(1, personService.getCurrentPerson().getIncomeSources().size());
    }

    @Test
    void testAddMaterialAid() {
        String input = "aid\nA1\n3000\nодноразова\nfalse\nДопомога\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        command.execute(List.of());
        assertEquals(1, personService.getCurrentPerson().getIncomeSources().size());
    }

    @Test
    void testAddRoyaltyIncome() {
        String input = "royalty\nR1\n5000\nКобзар\nкнига\nГонорар\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        command.execute(List.of());
        assertEquals(1, personService.getCurrentPerson().getIncomeSources().size());
    }

    @Test
    void testAddPropertySaleIncome() {
        String input = "sale\nS1\n100000\nквартира\ntrue\nПродаж\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        command.execute(List.of());
        assertEquals(1, personService.getCurrentPerson().getIncomeSources().size());
    }

    @Test
    void testAddForeignTransferIncome() {
        String input = "transfer\nT1\n20000\nНімеччина\nEUR\nПереказ\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        command.execute(List.of());
        assertEquals(1, personService.getCurrentPerson().getIncomeSources().size());
    }

    @Test
    void testUnknownTypeRejected() {
        String input = "unknown\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        command.execute(List.of());
        assertTrue(out.toString().contains("Невідомий"));
        assertEquals(0, personService.getCurrentPerson().getIncomeSources().size());
    }

    @Test
    void testInvalidAmountRetriesAndFails() {
        // Введемо невалідну суму, потім валідну
        String input = "employment\nI1\nnotanumber\n5000\nGoogle\ntrue\nЗарплата\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        command.execute(List.of());
        assertEquals(1, personService.getCurrentPerson().getIncomeSources().size());
    }

    @Test
    void testGetDescription() {
        assertNotNull(command.getDescription());
    }
}
