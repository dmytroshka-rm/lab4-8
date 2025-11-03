package taxsystem.domain;

public class ChildBenefit extends TaxBenefit {
    private int childCount;

    public ChildBenefit(String benefitId, double amount, String description, int childCount) {
        super(benefitId, amount, description);
        this.childCount = childCount;
    }

    @Override
    public double applyBenefit(double taxAmount) {
        return taxAmount;
    }

    @Override
    public boolean validateApplicability() {
        return isApplicable && childCount > 0 && amount > 0;
    }

    public int getChildCount() { return childCount; }
}