package taxsystem.ui.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taxsystem.service.TaxReportGenerator;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SetReportModeCommandTest {

    private TaxReportGenerator reportGenerator;
    private SetReportModeCommand command;
    private ByteArrayOutputStream out;

    @BeforeEach
    void setup() {
        reportGenerator = new TaxReportGenerator();
        command = new SetReportModeCommand(reportGenerator);
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
    }

    @Test
    void testNoParameters() {
        command.execute(List.of());
        assertTrue(out.toString().contains("Використання"));
    }

    @Test
    void testSetDetailedMode() {
        command.execute(List.of("detailed"));
        assertTrue(out.toString().contains("детальний"));
        assertTrue(reportGenerator.isIncludeDetails());
        assertEquals("DETAILED", reportGenerator.getReportFormat());
    }

    @Test
    void testSetSummaryMode() {
        command.execute(List.of("summary"));
        assertTrue(out.toString().contains("короткий"));
        assertFalse(reportGenerator.isIncludeDetails());
        assertEquals("SUMMARY", reportGenerator.getReportFormat());
    }

    @Test
    void testUnknownMode() {
        command.execute(List.of("xml"));
        assertTrue(out.toString().contains("Невідомий"));
    }

    @Test
    void testCaseInsensitive() {
        command.execute(List.of("DETAILED"));
        assertTrue(reportGenerator.isIncludeDetails());
    }

    @Test
    void testGetDescription() {
        assertNotNull(command.getDescription());
    }
}
