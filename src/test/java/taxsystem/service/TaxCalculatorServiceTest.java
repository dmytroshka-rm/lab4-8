package taxsystem.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taxsystem.domain.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaxCalculatorServiceTest {

    private TaxCalculatorService service;
    private Person p;

    @BeforeEach
    void setup() {
        service = new TaxCalculatorService();
        p = new Person("1", "Ivan", "Ivanov", "111");
        service.setCurrentPerson(p);
    }

    @Test
    void testGetTaxRule() {
        assertEquals(0.20, service.getTaxRule("ОПЛАТА_ПРАЦІ"));
        assertEquals(0.15, service.getTaxRule("ПОДАРУНОК"));
        assertEquals(0.18, service.getTaxRule("UNKNOWN")); // default
    }

    @Test
    void testSetCurrentPersonNullIgnored() {
        service.setCurrentPerson(null);
        assertNotNull(service.getCurrentPerson());
    }

    @Test
    void testAddIncomeNullIgnored() {
        service.addIncome(null);
        assertEquals(0, p.getIncomeSources().size());
    }

    @Test
    void testAddIncomeWorks() {
        EmploymentIncome i = new EmploymentIncome("1", 2000, "job", "Comp", true);
        service.addIncome(i);
        assertEquals(1, p.getIncomeSources().size());
    }

    @Test
    void testAddBenefitNullIgnored() {
        service.addBenefit(null);
        assertEquals(0, p.getTaxBenefits().size());
    }

    @Test
    void testAddBenefitWorks() {
        TaxBenefit b = new ChildBenefit("B1", 500, "child", 2);
        service.addBenefit(b);
        assertEquals(1, p.getTaxBenefits().size());
    }

    @Test
    void testRecalcTaxesNoPerson() {
        TaxCalculatorService s = new TaxCalculatorService();
        s.recalcTaxes(); // просто виведе повідомлення
    }

    @Test
    void testGetTotalTaxBeforeBenefits() {
        service.addIncome(new EmploymentIncome("1", 5000, "job", "Comp", true));
        service.recalcTaxes();
        assertEquals((5000 - 1000) * 0.20, service.getTotalTaxBeforeBenefits());
    }

    @Test
    void testApplyTaxBenefitsNullList() {
        assertEquals(1000, service.applyTaxBenefits(1000, null));
    }

    @Test
    void testApplyTaxBenefitsWithInactiveBenefit() {
        TaxBenefit b = new ChildBenefit("B1", 500, "child", 2);
        b.setActive(false); // 🔧 замість прямого доступу

        List<TaxBenefit> list = new ArrayList<>();
        list.add(b);

        double r = service.applyTaxBenefits(1000, list);
        assertEquals(1000, r); // пільга не застосовується
    }

    @Test
    void testApplyTaxBenefitsWorks() {
        TaxBenefit b = new ChildBenefit("B1", 200, "child", 2); // зменшує на 400
        List<TaxBenefit> list = new ArrayList<>();
        list.add(b);

        double r = service.applyTaxBenefits(500, list);
        assertEquals(100, r); // 500 − 400 = 100
    }

    @Test
    void testTotalTaxAfterBenefits() {
        TaxCalculatorService service = new TaxCalculatorService();
        Person p = new Person("P1", "Ivan", "Ivanov", "123");
        service.setCurrentPerson(p);

        service.addIncome(new EmploymentIncome("I1", 5000, "job", "Company", true));

        ChildBenefit b = new ChildBenefit("B1", 1000, "child", 2); // зменшення на 2000
        b.setActive(true); // важливо
        p.getTaxBenefits().add(b);

        service.recalcTaxes();

        assertEquals(0, service.getTotalTaxAfterBenefits());
    }

    @Test
    void testValidateTaxCalculationOk() {
        service.addIncome(new EmploymentIncome("1", 2000, "job", "Comp", true));
        service.addBenefit(new ChildBenefit("B1", 100, "child", 1));
        assertTrue(service.validateTaxCalculation(p));
    }

    @Test
    void testValidateNegativeIncome() {
        p.getIncomeSources().add(new EmploymentIncome("1", -10, "job", "Comp", true));
        assertFalse(service.validateTaxCalculation(p));
    }

    @Test
    void testValidateNegativeBenefit() {
        p.getTaxBenefits().add(new ChildBenefit("B1", -10, "child", 1));
        assertFalse(service.validateTaxCalculation(p));
    }

    @Test
    void testValidateNullPerson() {
        assertFalse(service.validateTaxCalculation(null));
    }
}
