package taxsystem.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PropertySaleIncomeTest {

    @Test
    void testConstructorAndGetters() {
        PropertySaleIncome income = new PropertySaleIncome("S1", 500000, "Продаж квартири", "квартира", true);

        assertEquals("S1", income.getSourceId());
        assertEquals(500000, income.getAmount());
        assertEquals("квартира", income.getPropertyType());
        assertTrue(income.isFirstSalePerYear());
    }

    @Test
    void testGetIncomeType() {
        PropertySaleIncome income = new PropertySaleIncome("S1", 500000, "Продаж", "авто", false);
        assertEquals("ПРОДАЖ_МАЙНА", income.getIncomeType());
    }

    @Test
    void testSetters() {
        PropertySaleIncome income = new PropertySaleIncome("S1", 500000, "Продаж", "квартира", true);

        income.setPropertyType("земля");
        assertEquals("земля", income.getPropertyType());

        income.setFirstSalePerYear(false);
        assertFalse(income.isFirstSalePerYear());
    }
}
