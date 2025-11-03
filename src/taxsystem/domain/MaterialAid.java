package taxsystem.domain;

public class MaterialAid extends IncomeSource {
    private String aidType;
    private boolean isTaxable;

    public MaterialAid(String sourceId, double amount, String description, String aidType, boolean isTaxable) {
        super(sourceId, amount, description);
        this.aidType = aidType;
        this.isTaxable = isTaxable;
    }

    @Override
    public double calculateTax() {
        this.taxAmount = isTaxable ? amount * 0.18 : 0;
        return taxAmount;
    }

    public String getAidType() { return aidType; }
    public boolean isTaxable() { return isTaxable; }
}