package taxsystem.ui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import taxsystem.ui.command.Command;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MenuTest {

    private final PrintStream originalOut = System.out;
    private final java.io.InputStream originalIn = System.in;

    @AfterEach
    void restore() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    private Command dummyCommand(String desc) {
        return new Command() {
            @Override
            public void execute(List<String> params) {
                System.out.println("EXECUTED:" + desc);
            }

            @Override
            public String getDescription() {
                return desc;
            }
        };
    }

    @Test
    void testMenuRunHelpAndExit() {
        System.setIn(new ByteArrayInputStream("help\nexit\n".getBytes()));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        Menu menu = new Menu();
        menu.addCommand("test", dummyCommand("Test command"));
        menu.run();

        String output = out.toString();
        assertTrue(output.contains("Доступні команди"));
        assertTrue(output.contains("test"));
        assertTrue(output.contains("Test command"));
    }

    @Test
    void testMenuRunExecutesCommand() {
        System.setIn(new ByteArrayInputStream("mycmd\nexit\n".getBytes()));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        Menu menu = new Menu();
        menu.addCommand("mycmd", dummyCommand("My cmd"));
        menu.run();

        assertTrue(out.toString().contains("EXECUTED:My cmd"));
    }

    @Test
    void testMenuRunUnknownCommand() {
        System.setIn(new ByteArrayInputStream("nosuchcmd\nexit\n".getBytes()));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        Menu menu = new Menu();
        menu.run();

        assertTrue(out.toString().contains("Невідома команда"));
    }

    @Test
    void testMenuRunEmptyInput() {
        System.setIn(new ByteArrayInputStream("\nexit\n".getBytes()));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        Menu menu = new Menu();
        menu.run();

        assertTrue(out.toString().contains("Вихід"));
    }

    @Test
    void testMenuRunCommandWithParams() {
        System.setIn(new ByteArrayInputStream("mycmd param1 param2\nexit\n".getBytes()));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        Menu menu = new Menu();
        menu.addCommand("mycmd", new Command() {
            @Override
            public void execute(List<String> params) {
                System.out.println("PARAMS:" + params.size());
            }

            @Override
            public String getDescription() {
                return "desc";
            }
        });
        menu.run();

        assertTrue(out.toString().contains("PARAMS:2"));
    }

    @Test
    void testMenuRunCommandThrowsException() {
        System.setIn(new ByteArrayInputStream("bad\nexit\n".getBytes()));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        Menu menu = new Menu();
        menu.addCommand("bad", new Command() {
            @Override
            public void execute(List<String> params) {
                throw new RuntimeException("test error");
            }

            @Override
            public String getDescription() {
                return "bad";
            }
        });
        menu.run();

        assertTrue(out.toString().contains("помилка") || out.toString().contains("Сталася"));
    }

    @Test
    void testShowHelp() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        Menu menu = new Menu();
        menu.addCommand("alpha", dummyCommand("Alpha desc"));
        menu.addCommand("beta", dummyCommand("Beta desc"));
        menu.showHelp();

        String output = out.toString();
        assertTrue(output.contains("alpha"));
        assertTrue(output.contains("beta"));
        assertTrue(output.contains("exit"));
    }
}
