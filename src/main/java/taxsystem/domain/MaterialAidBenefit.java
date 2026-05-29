package taxsystem.domain;

public class MaterialAidBenefit extends TaxBenefit {
    private double maxNonTaxableAmount;

    public MaterialAidBenefit(String benefitId, double amount, String description,
                              double maxNonTaxableAmount) {
        super(benefitId, amount, description);
        this.maxNonTaxableAmount = maxNonTaxableAmount;
    }

    @Override
    public double applyBenefit(double taxAmount) {
        double reduction = Math.min(amount, maxNonTaxableAmount);
        return Math.max(0, taxAmount - reduction);
    }

    @Override
    public boolean validateApplicability() {
        return active && amount > 0 && maxNonTaxableAmount > 0;
    }

    public double getMaxNonTaxableAmount() { return maxNonTaxableAmount; }
    public void setMaxNonTaxableAmount(double maxNonTaxableAmount) { this.maxNonTaxableAmount = maxNonTaxableAmount; }
}
