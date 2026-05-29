package taxsystem.ui;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

class UiStylesTest {

    @BeforeAll
    static void initJfx() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        try {
            Platform.startup(latch::countDown);
        } catch (IllegalStateException e) {
            // Already initialized
            latch.countDown();
        }
        latch.await();
    }

    @Test
    void testApplyAppTheme() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            Scene scene = new Scene(new StackPane(), 100, 100);
            UiStyles.applyAppTheme(scene);
            assertFalse(scene.getStylesheets().isEmpty());
            latch.countDown();
        });
        latch.await();
    }

    @Test
    void testStyleButtonPrimary() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            Button btn = new Button("Test");
            UiStyles.styleButton(btn, UiStyles.ButtonVariant.PRIMARY);
            assertTrue(btn.getStyleClass().contains("btn"));
            assertTrue(btn.getStyleClass().contains("btn-primary"));
            latch.countDown();
        });
        latch.await();
    }

    @Test
    void testStyleButtonAllVariants() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            for (UiStyles.ButtonVariant variant : UiStyles.ButtonVariant.values()) {
                Button btn = new Button("Test");
                UiStyles.styleButton(btn, variant);
                assertTrue(btn.getStyleClass().contains("btn"));
            }
            latch.countDown();
        });
        latch.await();
    }

    @Test
    void testButtonVariantValues() {
        UiStyles.ButtonVariant[] values = UiStyles.ButtonVariant.values();
        assertEquals(5, values.length);
        assertNotNull(UiStyles.ButtonVariant.valueOf("PRIMARY"));
        assertNotNull(UiStyles.ButtonVariant.valueOf("SUCCESS"));
        assertNotNull(UiStyles.ButtonVariant.valueOf("DANGER"));
        assertNotNull(UiStyles.ButtonVariant.valueOf("SECONDARY"));
        assertNotNull(UiStyles.ButtonVariant.valueOf("OUTLINE"));
    }
}
