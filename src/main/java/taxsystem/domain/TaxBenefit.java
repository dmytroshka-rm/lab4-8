package taxsystem.domain;

public abstract class TaxBenefit {
    protected String benefitId;
    protected double amount;
    protected String description;
    protected boolean active = true;

    public TaxBenefit(String benefitId, double amount, String description) {
        this.benefitId = benefitId;
        this.amount = amount;
        this.description = description;
    }

    public abstract double applyBenefit(double taxAmount);

    public abstract boolean validateApplicability();

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
