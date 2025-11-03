package taxsystem.service;

import taxsystem.domain.Person;
import taxsystem.domain.TaxBenefit;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class TaxCalculatorService {
    private Map<String, Double> taxRules;
    private int currentYear;
    private double nonTaxableMinimum;

    public TaxCalculatorService() {
        this.taxRules = new HashMap<>();
        this.currentYear = 2024;
        this.nonTaxableMinimum = 1000.0;
        initializeTaxRules();
    }

    private void initializeTaxRules() {
        taxRules.put("EMPLOYMENT", 0.18);
        taxRules.put("GIFT", 0.18);
        taxRules.put("MATERIAL_AID", 0.18);
    }

    public double calculateIncomeTax(double income) {
        return income * taxRules.get("EMPLOYMENT");
    }

    public double applyTaxBenefits(double taxAmount, List<TaxBenefit> benefits) {
        return 1;
    }

    public boolean validateTaxCalculation(Person person) {
        return true;
    }

    public double getTaxRule(String incomeType) {
        return 1;
    }

    public void setTaxRule(String incomeType, double rate) {
    }

    public int getCurrentYear() { return currentYear; }
    public double getNonTaxableMinimum() { return nonTaxableMinimum; }
}