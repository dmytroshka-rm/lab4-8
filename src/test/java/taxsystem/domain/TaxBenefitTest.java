package taxsystem.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaxBenefitTest {

    static class TestBenefit extends TaxBenefit {

        public TestBenefit(String id, double amount, String description) {
            super(id, amount, description);
        }

        @Override
        public double applyBenefit(double taxAmount) {
            return taxAmount - amount;
        }

        @Override
        public boolean validateApplicability() {
            return active && amount > 0;
        }
    }

    @Test
    void testGettersAndLogic() {
        TestBenefit benefit = new TestBenefit("B1", 100, "test");

        assertEquals(100, benefit.getAmount());
        assertEquals("test", benefit.getDescription());
        assertTrue(benefit.isActive());

        assertTrue(benefit.validateApplicability());
        assertEquals(900, benefit.applyBenefit(1000));
    }
}
