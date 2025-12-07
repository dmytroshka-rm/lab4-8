package taxsystem.domain;

import taxsystem.service.TaxCalculatorService;

public class EmploymentIncome extends IncomeSource {
    private String employerName;
    private boolean isMainJob;

    public EmploymentIncome(String sourceId, double amount, String description, String employerName, boolean isMainJob) {
        super(sourceId, amount, description);
        this.employerName = employerName;
        this.isMainJob = isMainJob;
    }

    @Override
    public double calculateTax(TaxCalculatorService tcs) {
        double nm = tcs.getNonTaxableMinimum();
        double rate = tcs.getTaxRule("ОПЛАТА_ПРАЦІ");
        double base = Math.max(0, amount - nm);
        this.taxAmount = base * rate;
        return taxAmount;
    }

}
