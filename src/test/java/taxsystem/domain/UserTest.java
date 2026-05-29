package taxsystem.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testConstructorAndGetters() {
        User user = new User("admin", "hash123", "admin@test.com");
        assertEquals("admin", user.getUsername());
        assertEquals("hash123", user.getPasswordHash());
        assertEquals("admin@test.com", user.getEmail());
    }

    @Test
    void testSetUsername() {
        User user = new User("old", "hash", "email@test.com");
        user.setUsername("newUser");
        assertEquals("newUser", user.getUsername());
    }

    @Test
    void testSetPasswordHash() {
        User user = new User("user", "oldHash", "email@test.com");
        user.setPasswordHash("newHash");
        assertEquals("newHash", user.getPasswordHash());
    }

    @Test
    void testSetEmail() {
        User user = new User("user", "hash", "old@test.com");
        user.setEmail("new@test.com");
        assertEquals("new@test.com", user.getEmail());
    }

    @Test
    void testNullEmail() {
        User user = new User("user", "hash", null);
        assertNull(user.getEmail());
    }
}
