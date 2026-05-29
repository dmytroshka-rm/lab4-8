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

        assertEquals("B1", benefit.getBenefitId());
        assertEquals(100, benefit.getAmount());
        assertEquals("test", benefit.getDescription());
        assertTrue(benefit.isActive());

        assertTrue(benefit.validateApplicability());
        assertEquals(900, benefit.applyBenefit(1000));
    }

    @Test
    void testSetters() {
        TestBenefit benefit = new TestBenefit("B1", 100, "test");

        benefit.setBenefitId("B2");
        assertEquals("B2", benefit.getBenefitId());

        benefit.setAmount(200);
        assertEquals(200, benefit.getAmount());

        benefit.setDescription("updated");
        assertEquals("updated", benefit.getDescription());

        benefit.setActive(false);
        assertFalse(benefit.isActive());
        assertFalse(benefit.validateApplicability());
    }
}
