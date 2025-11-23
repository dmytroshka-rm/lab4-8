package taxsystem.service;

import taxsystem.domain.*;
import java.util.*;

public class TaxCalculatorService {
    private Map<String, Double> taxRules;
    private int currentYear;
    private double nonTaxableMinimum;
    private Person currentPerson;

    public TaxCalculatorService() {
        this.taxRules = new HashMap<>();
        this.currentYear = 2025;
        this.nonTaxableMinimum = 1000.0;
        initializeTaxRules();
        this.currentPerson = null;
    }

    private void initializeTaxRules() {
        taxRules.put("ОПЛАТА_ПРАЦІ", 0.20);
        taxRules.put("ПОДАРУНОК", 0.15);
        taxRules.put("МАТЕРІАЛЬНА_ДОПОМОГА", 0.18);
    }

    public double getTaxRule(String incomeType) {
        return taxRules.getOrDefault(incomeType, 0.18);
    }

    public double getNonTaxableMinimum() {
        return nonTaxableMinimum;
    }

    public Person getCurrentPerson() {
        return currentPerson;
    }

    public void setCurrentPerson(Person p) {
        if (p != null) this.currentPerson = p;
    }

    public void addIncome(IncomeSource s) {
        if (currentPerson != null && s != null) {
            currentPerson.getIncomeSources().add(s);
        }
    }

    public void addBenefit(TaxBenefit b) {
        if (currentPerson != null && b != null) {
            currentPerson.getTaxBenefits().add(b);
        }
    }

    public void recalcTaxes() {
        if (currentPerson == null) {
            System.out.println("Спочатку створіть особу.");
            return;
        }

        for (IncomeSource s : currentPerson.getIncomeSources()) {
            s.calculateTax(this);
        }
    }

    public double getTotalTaxBeforeBenefits() {
        if (currentPerson == null) return 0;

        double total = 0;
        for (IncomeSource source : currentPerson.getIncomeSources()) {
            total += source.getTaxAmount();
        }
        return total;
    }

    public double applyTaxBenefits(double taxAmount, List<TaxBenefit> benefits) {
        double result = taxAmount;
        if (benefits == null) return result;
        for (TaxBenefit b : benefits) {
            if (b != null && b.validateApplicability()) {
                result = b.applyBenefit(result);
            }
        }
        return Math.max(0, result);
    }

    public double getTotalTaxAfterBenefits() {
        if (currentPerson == null) return 0;
        return applyTaxBenefits(
                getTotalTaxBeforeBenefits(),
                currentPerson.getTaxBenefits()
        );
    }

    public boolean validateTaxCalculation(Person person) {
        if (person == null) return false;
        for (IncomeSource s : person.getIncomeSources()) {
            if (s.getAmount() < 0) return false;
        }
        for (TaxBenefit b : person.getTaxBenefits()) {
            if (b.getAmount() < 0) return false;
        }
        return true;
    }

}
