package taxsystem.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoyaltyIncomeTest {

    @Test
    void testConstructorAndGetters() {
        RoyaltyIncome income = new RoyaltyIncome("R1", 5000, "Гонорар", "Кобзар", "книга");

        assertEquals("R1", income.getSourceId());
        assertEquals(5000, income.getAmount());
        assertEquals("Гонорар", income.getDescription());
        assertEquals("Кобзар", income.getWorkTitle());
        assertEquals("книга", income.getWorkType());
    }

    @Test
    void testGetIncomeType() {
        RoyaltyIncome income = new RoyaltyIncome("R1", 5000, "Гонорар", "Кобзар", "книга");
        assertEquals("АВТОРСЬКА_ВИНАГОРОДА", income.getIncomeType());
    }

    @Test
    void testSetters() {
        RoyaltyIncome income = new RoyaltyIncome("R1", 5000, "Гонорар", "Кобзар", "книга");

        income.setWorkTitle("Нова книга");
        assertEquals("Нова книга", income.getWorkTitle());

        income.setWorkType("стаття");
        assertEquals("стаття", income.getWorkType());
    }
}
