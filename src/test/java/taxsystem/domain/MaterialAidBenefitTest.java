package taxsystem.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MaterialAidBenefitTest {

    @Test
    void testApplyBenefitReducesTax() {
        MaterialAidBenefit benefit = new MaterialAidBenefit("M1", 2000, "Мат. допомога", 4470);
        double result = benefit.applyBenefit(5000);
        assertEquals(3000, result);
    }

    @Test
    void testApplyBenefitCappedByMaxNonTaxable() {
        MaterialAidBenefit benefit = new MaterialAidBenefit("M1", 5000, "Мат. допомога", 3000);
        double result = benefit.applyBenefit(5000);
        assertEquals(2000, result);
    }

    @Test
    void testApplyBenefitNotBelowZero() {
        MaterialAidBenefit benefit = new MaterialAidBenefit("M1", 10000, "Мат. допомога", 10000);
        double result = benefit.applyBenefit(3000);
        assertEquals(0, result);
    }

    @Test
    void testValidateApplicabilityTrue() {
        MaterialAidBenefit benefit = new MaterialAidBenefit("M1", 2000, "Мат. допомога", 4470);
        assertTrue(benefit.validateApplicability());
    }

    @Test
    void testValidateApplicabilityFalseWhenInactive() {
        MaterialAidBenefit benefit = new MaterialAidBenefit("M1", 2000, "Мат. допомога", 4470);
        benefit.setActive(false);
        assertFalse(benefit.validateApplicability());
    }

    @Test
    void testValidateApplicabilityFalseWhenZeroAmount() {
        MaterialAidBenefit benefit = new MaterialAidBenefit("M1", 0, "Мат. допомога", 4470);
        assertFalse(benefit.validateApplicability());
    }

    @Test
    void testGettersAndSetters() {
        MaterialAidBenefit benefit = new MaterialAidBenefit("M1", 2000, "Мат. допомога", 4470);

        assertEquals(4470, benefit.getMaxNonTaxableAmount());

        benefit.setMaxNonTaxableAmount(5000);
        assertEquals(5000, benefit.getMaxNonTaxableAmount());
    }
}
