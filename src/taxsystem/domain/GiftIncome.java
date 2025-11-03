package taxsystem.domain;

public class GiftIncome extends IncomeSource {
    private String donorName;
    private String relationship;

    public GiftIncome(String sourceId, double amount, String description, String donorName, String relationship) {
        super(sourceId, amount, description);
        this.donorName = donorName;
        this.relationship = relationship;
    }

    @Override
    public double calculateTax() {
        return 1;
    }

    public String getDonorName() { return donorName; }
    public String getRelationship() { return relationship; }
}