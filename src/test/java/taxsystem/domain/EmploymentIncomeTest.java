package taxsystem.domain;

import org.junit.jupiter.api.Test;
import taxsystem.service.TaxCalculatorService;

import static org.junit.jupiter.api.Assertions.*;

class EmploymentIncomeTest {

    @Test
    void testCalculateTaxAboveNonTaxableMinimum() {
        TaxCalculatorService service = new TaxCalculatorService();
        EmploymentIncome income = new EmploymentIncome("I1", 5000, "job", "Company", true);

        double tax = income.calculateTax(service);

        double expected = (5000 - service.getNonTaxableMinimum()) * service.getTaxRule("ОПЛАТА_ПРАЦІ");
        assertEquals(expected, tax);
    }

    @Test
    void testCalculateTaxBelowNonTaxableMinimum() {
        TaxCalculatorService service = new TaxCalculatorService();
        EmploymentIncome income = new EmploymentIncome("I1", 500, "job", "Company", true);

        double tax = income.calculateTax(service);

        assertEquals(0.0, tax);
    }
}
