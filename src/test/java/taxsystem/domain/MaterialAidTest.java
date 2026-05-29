package taxsystem.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MaterialAidTest {

    @Test
    void testConstructorAndGetters() {
        MaterialAid aid = new MaterialAid("A1", 5000, "aid", "одноразова", false);

        assertEquals("A1", aid.getSourceId());
        assertEquals(5000, aid.getAmount());
        assertEquals("aid", aid.getDescription());
        assertEquals("одноразова", aid.getAidType());
        assertFalse(aid.isTaxable());
    }

    @Test
    void testGetIncomeType() {
        MaterialAid aid = new MaterialAid("A1", 5000, "aid", "одноразова", true);
        assertEquals("МАТЕРІАЛЬНА_ДОПОМОГА", aid.getIncomeType());
    }

    @Test
    void testSetters() {
        MaterialAid aid = new MaterialAid("A1", 5000, "aid", "одноразова", false);

        aid.setAidType("соціальна");
        assertEquals("соціальна", aid.getAidType());

        aid.setTaxable(true);
        assertTrue(aid.isTaxable());
    }
}
