package taxsystem.domain;

import taxsystem.service.TaxCalculatorService;

public class MaterialAid extends IncomeSource {
    private String aidType;
    private boolean isTaxable;

    public MaterialAid(String sourceId, double amount, String description, String aidType, boolean isTaxable) {
        super(sourceId, amount, description);
        this.aidType = aidType;
        this.isTaxable = isTaxable;
    }

    @Override
    public double calculateTax(TaxCalculatorService matHelp) {
        if (!isTaxable) {
            this.taxAmount = 0.0;
        } else {
            double rate = matHelp.getTaxRule("МАТЕРІАЛЬНА_ДОПОМОГА");
            this.taxAmount = amount * rate;
        }
        return taxAmount;
    }

}
