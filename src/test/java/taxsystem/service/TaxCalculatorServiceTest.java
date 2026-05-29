package taxsystem.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taxsystem.domain.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaxCalculatorServiceTest {

    private TaxCalculatorService service;
    private Person person;

    @BeforeEach
    void setup() {
        service = new TaxCalculatorService();
        person = new Person("1", "Ivan", "Ivanov", "111");
    }

    @Test
    void testGetTaxRule() {
        assertEquals(0.20, service.getTaxRule("ОПЛАТА_ПРАЦІ"));
        assertEquals(0.15, service.getTaxRule("ПОДАРУНОК"));
        assertEquals(0.18, service.getTaxRule("UNKNOWN"));
    }

    @Test
    void testGetNonTaxableMinimum() {
        assertEquals(1000.0, service.getNonTaxableMinimum());
    }

    @Test
    void testCalculateTaxForEmploymentIncomeAboveMinimum() {
        EmploymentIncome income = new EmploymentIncome("I1", 5000, "job", "Company", true);
        double tax = service.calculateTaxForIncome(income);
        double expected = (5000 - 1000) * 0.20;
        assertEquals(expected, tax);
        assertEquals(expected, income.getTaxAmount());
    }

    @Test
    void testCalculateTaxForEmploymentIncomeBelowMinimum() {
        EmploymentIncome income = new EmploymentIncome("I1", 500, "job", "Company", true);
        double tax = service.calculateTaxForIncome(income);
        assertEquals(0.0, tax);
    }

    @Test
    void testCalculateTaxForGiftCloseRelative() {
        GiftIncome income = new GiftIncome("G1", 10000, "gift", "Mom", "мати");
        double tax = service.calculateTaxForIncome(income);
        assertEquals(0.0, tax);
    }

    @Test
    void testCalculateTaxForGiftNonRelative() {
        GiftIncome income = new GiftIncome("G1", 10000, "gift", "Friend", "друг");
        double tax = service.calculateTaxForIncome(income);
        assertEquals(10000 * 0.15, tax);
    }

    @Test
    void testCalculateTaxForGiftNullRelationship() {
        GiftIncome income = new GiftIncome("G1", 10000, "gift", "Unknown", null);
        double tax = service.calculateTaxForIncome(income);
        assertEquals(10000 * 0.15, tax);
    }

    @Test
    void testCalculateTaxForNonTaxableMaterialAid() {
        MaterialAid aid = new MaterialAid("A1", 5000, "aid", "одноразова", false);
        double tax = service.calculateTaxForIncome(aid);
        assertEquals(0.0, tax);
    }

    @Test
    void testCalculateTaxForTaxableMaterialAid() {
        MaterialAid aid = new MaterialAid("A1", 5000, "aid", "одноразова", true);
        double tax = service.calculateTaxForIncome(aid);
        assertEquals(5000 * 0.18, tax);
    }

    @Test
    void testRecalcTaxesNullPerson() {
        service.recalcTaxes(null);
    }

    @Test
    void testRecalcTaxesCalculatesAll() {
        person.getIncomeSources().add(new EmploymentIncome("I1", 5000, "job", "Comp", true));
        person.getIncomeSources().add(new GiftIncome("G1", 3000, "gift", "Friend", "друг"));

        service.recalcTaxes(person);

        assertEquals((5000 - 1000) * 0.20, person.getIncomeSources().get(0).getTaxAmount());
        assertEquals(3000 * 0.15, person.getIncomeSources().get(1).getTaxAmount());
    }

    @Test
    void testGetTotalTaxBeforeBenefits() {
        person.getIncomeSources().add(new EmploymentIncome("I1", 5000, "job", "Comp", true));
        service.recalcTaxes(person);
        assertEquals((5000 - 1000) * 0.20, service.getTotalTaxBeforeBenefits(person));
    }

    @Test
    void testGetTotalTaxBeforeBenefitsNullPerson() {
        assertEquals(0, service.getTotalTaxBeforeBenefits(null));
    }

    @Test
    void testApplyTaxBenefitsNullList() {
        assertEquals(1000, service.applyTaxBenefits(1000, null));
    }

    @Test
    void testApplyTaxBenefitsWithInactiveBenefit() {
        TaxBenefit b = new ChildBenefit("B1", 500, "child", 2);
        b.setActive(false);

        List<TaxBenefit> list = new ArrayList<>();
        list.add(b);

        double r = service.applyTaxBenefits(1000, list);
        assertEquals(1000, r);
    }

    @Test
    void testApplyTaxBenefitsWorks() {
        TaxBenefit b = new ChildBenefit("B1", 200, "child", 2);
        List<TaxBenefit> list = new ArrayList<>();
        list.add(b);

        double r = service.applyTaxBenefits(500, list);
        assertEquals(100, r);
    }

    @Test
    void testTotalTaxAfterBenefits() {
        person.getIncomeSources().add(new EmploymentIncome("I1", 5000, "job", "Company", true));
        person.getTaxBenefits().add(new ChildBenefit("B1", 1000, "child", 2));

        service.recalcTaxes(person);

        assertEquals(0, service.getTotalTaxAfterBenefits(person));
    }

    @Test
    void testTotalTaxAfterBenefitsNullPerson() {
        assertEquals(0, service.getTotalTaxAfterBenefits(null));
    }

    @Test
    void testCalculateTaxForRoyaltyIncome() {
        RoyaltyIncome income = new RoyaltyIncome("R1", 10000, "Гонорар", "Книга", "книга");
        double tax = service.calculateTaxForIncome(income);
        assertEquals(10000 * 0.18, tax);
    }

    @Test
    void testCalculateTaxForPropertySaleFirstSale() {
        PropertySaleIncome income = new PropertySaleIncome("S1", 100000, "Квартира", "квартира", true);
        double tax = service.calculateTaxForIncome(income);
        assertEquals(100000 * 0.05, tax);
    }

    @Test
    void testCalculateTaxForPropertySaleRepeatSale() {
        PropertySaleIncome income = new PropertySaleIncome("S1", 100000, "Авто", "авто", false);
        double tax = service.calculateTaxForIncome(income);
        assertEquals(100000 * 0.18, tax);
    }

    @Test
    void testCalculateTaxForForeignTransfer() {
        ForeignTransferIncome income = new ForeignTransferIncome("T1", 20000, "Переказ", "Німеччина", "EUR");
        double tax = service.calculateTaxForIncome(income);
        assertEquals(20000 * 0.18, tax);
    }

    @Test
    void testValidateTaxCalculationOk() {
        person.getIncomeSources().add(new EmploymentIncome("1", 2000, "job", "Comp", true));
        person.getTaxBenefits().add(new ChildBenefit("B1", 100, "child", 1));
        assertTrue(service.validateTaxCalculation(person));
    }

    @Test
    void testValidateNegativeIncome() {
        person.getIncomeSources().add(new EmploymentIncome("1", -10, "job", "Comp", true));
        assertFalse(service.validateTaxCalculation(person));
    }

    @Test
    void testValidateNegativeBenefit() {
        person.getTaxBenefits().add(new ChildBenefit("B1", -10, "child", 1));
        assertFalse(service.validateTaxCalculation(person));
    }

    @Test
    void testValidateNullPerson() {
        assertFalse(service.validateTaxCalculation(null));
    }

    @Test
    void testSortByTaxAscending() {
        person.getIncomeSources().add(new EmploymentIncome("I1", 5000, "big job", "Comp", true));
        person.getIncomeSources().add(new EmploymentIncome("I2", 2000, "small job", "Comp", false));
        service.recalcTaxes(person);

        List<IncomeSource> sorted = service.sortByTax(person, true);
        assertTrue(sorted.get(0).getTaxAmount() <= sorted.get(1).getTaxAmount());
    }

    @Test
    void testSortByTaxDescending() {
        person.getIncomeSources().add(new EmploymentIncome("I1", 5000, "big job", "Comp", true));
        person.getIncomeSources().add(new EmploymentIncome("I2", 2000, "small job", "Comp", false));
        service.recalcTaxes(person);

        List<IncomeSource> sorted = service.sortByTax(person, false);
        assertTrue(sorted.get(0).getTaxAmount() >= sorted.get(1).getTaxAmount());
    }

    @Test
    void testFindByTaxRange() {
        person.getIncomeSources().add(new EmploymentIncome("I1", 5000, "big", "Comp", true));
        person.getIncomeSources().add(new EmploymentIncome("I2", 1500, "small", "Comp", false));
        service.recalcTaxes(person);

        List<IncomeSource> found = service.findByTaxRange(person, 50, 200);
        assertEquals(1, found.size());
        assertEquals("small", found.get(0).getDescription());
    }

    @Test
    void testSortByTaxNullPerson() {
        assertTrue(service.sortByTax(null, true).isEmpty());
    }

    @Test
    void testFindByTaxRangeNullPerson() {
        assertTrue(service.findByTaxRange(null, 0, 1000).isEmpty());
    }
}
