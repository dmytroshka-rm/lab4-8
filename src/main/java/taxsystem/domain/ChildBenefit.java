package taxsystem.domain;

public class ChildBenefit extends TaxBenefit {

    private int childCount;

    public ChildBenefit(String benefitId, double amount, String description, int childCount) {
        super(benefitId, amount, description);
        this.childCount = childCount;
    }

    @Override
    public double applyBenefit(double taxAmount) {
        double reduced = taxAmount - (amount * childCount);
        return Math.max(0, reduced);
    }

    @Override
    public boolean validateApplicability() {
        return active && childCount > 0 && amount > 0;
    }

}
