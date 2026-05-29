package taxsystem;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationTest {

    private final PrintStream originalOut = System.out;
    private final java.io.InputStream originalIn = System.in;

    @AfterEach
    void restore() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    @Test
    void testConstructorInitializesSuccessfully() {
        Application app = new Application();
        assertNotNull(app);
    }

    @Test
    void testStartRunsMenuAndExits() {
        System.setIn(new ByteArrayInputStream("exit\n".getBytes()));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        Application app = new Application();
        app.start();

        assertTrue(out.toString().contains("Вихід"));
    }

    @Test
    void testMainMethodRuns() {
        System.setIn(new ByteArrayInputStream("exit\n".getBytes()));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        assertDoesNotThrow(() -> Application.main(new String[]{}));
    }

    @Test
    void testHelpCommandInMenu() {
        System.setIn(new ByteArrayInputStream("help\nexit\n".getBytes()));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        Application app = new Application();
        app.start();

        String output = out.toString();
        assertTrue(output.contains("create_person") || output.contains("Доступні"));
    }
}
