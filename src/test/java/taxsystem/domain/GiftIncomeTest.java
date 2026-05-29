package taxsystem.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GiftIncomeTest {

    @Test
    void testCloseRelative() {
        GiftIncome income = new GiftIncome("G1", 10000, "gift", "Mom", "мати");
        assertTrue(income.isCloseRelative());
    }

    @Test
    void testNonRelative() {
        GiftIncome income = new GiftIncome("G1", 10000, "gift", "Friend", "друг");
        assertFalse(income.isCloseRelative());
    }

    @Test
    void testNullRelationship() {
        GiftIncome income = new GiftIncome("G1", 10000, "gift", "Unknown", null);
        assertFalse(income.isCloseRelative());
    }

    @Test
    void testGetIncomeType() {
        GiftIncome income = new GiftIncome("G1", 10000, "gift", "Mom", "мати");
        assertEquals("ПОДАРУНОК", income.getIncomeType());
    }

    @Test
    void testGettersAndSetters() {
        GiftIncome income = new GiftIncome("G1", 10000, "gift", "Mom", "мати");

        assertEquals("Mom", income.getDonorName());
        assertEquals("мати", income.getRelationship());

        income.setDonorName("Dad");
        income.setRelationship("батько");
        assertEquals("Dad", income.getDonorName());
        assertEquals("батько", income.getRelationship());
        assertTrue(income.isCloseRelative());
    }
}
