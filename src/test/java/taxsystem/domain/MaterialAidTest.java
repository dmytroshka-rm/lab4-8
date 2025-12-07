package taxsystem.domain;

import org.junit.jupiter.api.Test;
import taxsystem.service.TaxCalculatorService;

import static org.junit.jupiter.api.Assertions.*;

class MaterialAidTest {

    @Test
    void testNonTaxableAidHasZeroTax() {
        TaxCalculatorService service = new TaxCalculatorService();
        MaterialAid aid = new MaterialAid("A1", 5000, "aid", "одноразова", false);

        double tax = aid.calculateTax(service);

        assertEquals(0.0, tax);
    }

    @Test
    void testTaxableAidUsesCorrectRate() {
        TaxCalculatorService service = new TaxCalculatorService();
        MaterialAid aid = new MaterialAid("A1", 5000, "aid", "одноразова", true);

        double tax = aid.calculateTax(service);

        assertEquals(5000 * service.getTaxRule("МАТЕРІАЛЬНА_ДОПОМОГА"), tax);
    }
}
