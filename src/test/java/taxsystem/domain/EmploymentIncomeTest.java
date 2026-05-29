package taxsystem.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmploymentIncomeTest {

    @Test
    void testConstructorAndGetters() {
        EmploymentIncome income = new EmploymentIncome("I1", 5000, "job", "Company", true);

        assertEquals("I1", income.getSourceId());
        assertEquals(5000, income.getAmount());
        assertEquals("job", income.getDescription());
        assertEquals("Company", income.getEmployerName());
        assertTrue(income.isMainJob());
        assertEquals(0.0, income.getTaxAmount());
    }

    @Test
    void testGetIncomeType() {
        EmploymentIncome income = new EmploymentIncome("I1", 5000, "job", "Company", true);
        assertEquals("ОПЛАТА_ПРАЦІ", income.getIncomeType());
    }

    @Test
    void testSetters() {
        EmploymentIncome income = new EmploymentIncome("I1", 5000, "job", "Company", true);

        income.setEmployerName("NewCompany");
        assertEquals("NewCompany", income.getEmployerName());

        income.setMainJob(false);
        assertFalse(income.isMainJob());

        income.setAmount(10000);
        assertEquals(10000, income.getAmount());

        income.setSourceId("I2");
        assertEquals("I2", income.getSourceId());

        income.setDescription("updated desc");
        assertEquals("updated desc", income.getDescription());

        income.setTaxAmount(500);
        assertEquals(500, income.getTaxAmount());
    }
}
