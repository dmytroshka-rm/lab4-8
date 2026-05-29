package taxsystem.domain;

import java.util.Set;

public class GiftIncome extends IncomeSource {
    private static final Set<String> CLOSE_RELATIVES = Set.of(
            "близька родина", "подружжя", "батько", "мати",
            "дитина", "син", "донька", "дочка", "брат", "сестра"
    );

    private String donorName;
    private String relationship;

    public GiftIncome(String sourceId, double amount, String description, String donorName, String relationship) {
        super(sourceId, amount, description);
        this.donorName = donorName;
        this.relationship = relationship;
    }

    @Override
    public String getIncomeType() {
        return "ПОДАРУНОК";
    }

    public boolean isCloseRelative() {
        if (relationship == null) return false;
        return CLOSE_RELATIVES.contains(relationship.trim().toLowerCase());
    }

    public String getDonorName() { return donorName; }
    public void setDonorName(String donorName) { this.donorName = donorName; }
    public String getRelationship() { return relationship; }
    public void setRelationship(String relationship) { this.relationship = relationship; }
}
