package taxsystem.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taxsystem.domain.User;
import taxsystem.repository.DatabaseManager;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() throws IOException {
        // Тимчасовий файл SQLite: schema зберігається між з'єднаннями, видаляється при виході JVM.
        File tmpDb = File.createTempFile("usertest_", ".db");
        tmpDb.deleteOnExit();
        DatabaseManager dbManager = new DatabaseManager(tmpDb.getAbsolutePath());
        userService = new UserService(dbManager);
    }

    // ── Реєстрація ────────────────────────────────────────────────────────

    @Test
    void registerSuccess() {
        String error = userService.register("alice", "pass123", "alice@test.com");
        assertNull(error, "Реєстрація має повернути null при успіху");
    }

    @Test
    void registerWithNullEmailSuccess() {
        String error = userService.register("bob", "pass1234", null);
        assertNull(error, "Реєстрація з null email має проходити успішно");
    }

    @Test
    void registerEmptyUsernameReturnsError() {
        String error = userService.register("", "pass123", "");
        assertNotNull(error);
        assertTrue(error.contains("порожн"), "Повідомлення про порожній логін");
    }

    @Test
    void registerNullUsernameReturnsError() {
        String error = userService.register(null, "pass123", "");
        assertNotNull(error);
    }

    @Test
    void registerShortPasswordReturnsError() {
        String error = userService.register("carol", "abc", "");
        assertNotNull(error);
        assertTrue(error.contains("4"), "Повідомлення про мін. довжину пароля");
    }

    @Test
    void registerNullPasswordReturnsError() {
        String error = userService.register("dave", null, "");
        assertNotNull(error);
    }

    @Test
    void registerDuplicateUsernameReturnsError() {
        userService.register("alice", "pass123", "");
        String error = userService.register("alice", "other1234", "");
        assertNotNull(error);
        assertTrue(error.contains("існує") || error.contains("вже"), "Помилка дублювання логіну");
    }

    // ── Вхід ─────────────────────────────────────────────────────────────

    @Test
    void loginSuccess() {
        userService.register("alice", "pass123", "");
        User user = userService.login("alice", "pass123");
        assertNotNull(user, "Логін з вірним паролем має повертати User");
        assertEquals("alice", user.getUsername());
    }

    @Test
    void loginWrongPasswordReturnsNull() {
        userService.register("alice", "pass123", "");
        User user = userService.login("alice", "wrongpass");
        assertNull(user, "Невірний пароль → null");
    }

    @Test
    void loginUnknownUserReturnsNull() {
        User user = userService.login("nobody", "pass123");
        assertNull(user, "Незнайомий логін → null");
    }

    @Test
    void loginNullUsernameReturnsNull() {
        User user = userService.login(null, "pass123");
        assertNull(user);
    }

    @Test
    void loginNullPasswordReturnsNull() {
        userService.register("alice", "pass123", "");
        User user = userService.login("alice", null);
        assertNull(user);
    }

    @Test
    void loginAfterDuplicateRegistrationUsesFirstPassword() {
        userService.register("alice", "first123", "");
        userService.register("alice", "second99", ""); // має повернути помилку, не перезаписати
        User user = userService.login("alice", "first123");
        assertNotNull(user, "Перший пароль має залишатись після невдалої дублікатної реєстрації");
    }
}
