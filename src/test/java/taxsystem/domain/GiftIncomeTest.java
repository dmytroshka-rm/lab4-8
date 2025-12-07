package taxsystem.domain;

import org.junit.jupiter.api.Test;
import taxsystem.service.TaxCalculatorService;

import static org.junit.jupiter.api.Assertions.*;

class GiftIncomeTest {

    @Test
    void testCloseRelative_NoTax() {
        TaxCalculatorService service = new TaxCalculatorService();
        GiftIncome income = new GiftIncome("G1", 10000, "gift", "Mom", "мати");

        double tax = income.calculateTax(service);

        assertEquals(0.0, tax);
    }

    @Test
    void testNonRelative_TaxApplied() {
        TaxCalculatorService service = new TaxCalculatorService();
        GiftIncome income = new GiftIncome("G1", 10000, "gift", "Friend", "друг");

        double tax = income.calculateTax(service);

        assertEquals(10000 * service.getTaxRule("ПОДАРУНОК"), tax);
    }

    @Test
    void testNullRelationship_TreatedAsNonRelative() {
        TaxCalculatorService service = new TaxCalculatorService();
        GiftIncome income = new GiftIncome("G1", 10000, "gift", "Unknown", null);

        double tax = income.calculateTax(service);

        assertEquals(10000 * service.getTaxRule("ПОДАРУНОК"), tax);
    }
}
