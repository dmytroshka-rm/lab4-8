package taxsystem.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChildBenefitTest {

    @Test
    void testApplyBenefitReducesTax() {
        ChildBenefit benefit = new ChildBenefit("B1", 1000, "child", 2);
        double result = benefit.applyBenefit(5000);
        assertEquals(3000, result);
    }

    @Test
    void testApplyBenefitNotBelowZero() {
        ChildBenefit benefit = new ChildBenefit("B1", 1000, "child", 10);
        double result = benefit.applyBenefit(2000);
        assertEquals(0, result);
    }

    @Test
    void testValidateApplicabilityTrue() {
        ChildBenefit benefit = new ChildBenefit("B1", 1000, "child", 2);
        assertTrue(benefit.validateApplicability());
    }

    @Test
    void testValidateApplicabilityFalseWhenInactive() {
        ChildBenefit benefit = new ChildBenefit("B1", 1000, "child", 2);
        benefit.active = false;
        assertFalse(benefit.validateApplicability());
    }

    @Test
    void testValidateApplicabilityFalseWhenInvalidValues() {
        ChildBenefit benefit = new ChildBenefit("B1", 0, "child", 0);
        assertFalse(benefit.validateApplicability());
    }
}
