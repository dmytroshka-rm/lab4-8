package taxsystem.ui;

import org.junit.jupiter.api.Test;
import taxsystem.ui.command.Command;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MenuTest {

    @Test
    void testMenuRunReadsInput() {
        String fakeInput = "help\nexit\n";
        System.setIn(new ByteArrayInputStream(fakeInput.getBytes()));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        Menu menu = new Menu();
        menu.addCommand("test", new Command() {
            @Override
            public void execute(List<String> params) {
                System.out.println("CMD");
            }

            @Override
            public String getDescription() {
                return "Test command";
            }
        });

        menu.run();

        String output = out.toString();

        assertTrue(output.contains("Доступні команди"));
        assertTrue(output.contains("exit"));
    }
}
