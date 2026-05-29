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
    public String getIncomeType() {
        return "МАТЕРІАЛЬНА_ДОПОМОГА";
    }

    public String getAidType() { return aidType; }
    public void setAidType(String aidType) { this.aidType = aidType; }
    public boolean isTaxable() { return isTaxable; }
    public void setTaxable(boolean taxable) { isTaxable = taxable; }
}
