package taxsystem.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ForeignTransferIncomeTest {

    @Test
    void testConstructorAndGetters() {
        ForeignTransferIncome income = new ForeignTransferIncome("T1", 10000, "Переказ", "Німеччина", "EUR");

        assertEquals("T1", income.getSourceId());
        assertEquals(10000, income.getAmount());
        assertEquals("Німеччина", income.getCountry());
        assertEquals("EUR", income.getCurrency());
    }

    @Test
    void testGetIncomeType() {
        ForeignTransferIncome income = new ForeignTransferIncome("T1", 10000, "Переказ", "США", "USD");
        assertEquals("ПЕРЕКАЗ_З_ЗАКОРДОНУ", income.getIncomeType());
    }

    @Test
    void testSetters() {
        ForeignTransferIncome income = new ForeignTransferIncome("T1", 10000, "Переказ", "Німеччина", "EUR");

        income.setCountry("Польща");
        assertEquals("Польща", income.getCountry());

        income.setCurrency("PLN");
        assertEquals("PLN", income.getCurrency());
    }
}
