package taxsystem.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import taxsystem.domain.User;
import taxsystem.repository.DatabaseManager;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserService {

    private static final Logger log = LogManager.getLogger(UserService.class);

    private final DatabaseManager dbManager;

    public UserService(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    // ──────────────────────────────────────────────────────────
    //  Реєстрація
    // ──────────────────────────────────────────────────────────

    /**
     * @return null при успіху, рядок з помилкою при невдачі
     */
    public String register(String username, String password, String email) {
        if (username == null || username.isBlank()) return "Логін не може бути порожнім.";
        if (password == null || password.length() < 4)  return "Пароль має містити щонайменше 4 символи.";

        if (findByUsername(username) != null) {
            log.warn("Спроба реєстрації вже існуючого логіну: {}", username);
            return "Користувач із таким логіном вже існує.";
        }

        String hash = sha256(password);
        String sql = "INSERT INTO users (username, password_hash, email) VALUES (?, ?, ?)";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, hash);
            ps.setString(3, email == null ? "" : email);
            ps.executeUpdate();
            log.info("Зареєстровано нового користувача: {}", username);
            return null;

        } catch (SQLException e) {
            log.error("Помилка реєстрації користувача '{}'", username, e);
            return "Помилка бази даних: " + e.getMessage();
        }
    }

    // ──────────────────────────────────────────────────────────
    //  Вхід
    // ──────────────────────────────────────────────────────────

    /**
     * @return User при успіху, null при невдачі
     */
    public User login(String username, String password) {
        if (username == null || password == null) return null;

        User user = findByUsername(username);
        if (user == null) {
            log.warn("Спроба входу з неіснуючим логіном: {}", username);
            return null;
        }

        if (!user.getPasswordHash().equals(sha256(password))) {
            log.warn("Невірний пароль для користувача: {}", username);
            return null;
        }

        log.info("Успішний вхід: {}", username);
        return user;
    }

    // ──────────────────────────────────────────────────────────
    //  Допоміжні методи
    // ──────────────────────────────────────────────────────────

    private User findByUsername(String username) {
        String sql = "SELECT username, password_hash, email FROM users WHERE username = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getString("username"),
                                   rs.getString("password_hash"),
                                   rs.getString("email"));
                }
            }
        } catch (SQLException e) {
            log.error("Помилка пошуку користувача '{}'", username, e);
        }
        return null;
    }

    private static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 недоступний", e);
        }
    }
}
