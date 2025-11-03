package taxsystem.domain;

public abstract class IncomeSource {
    protected String sourceId;
    protected double amount;
    protected double taxAmount;
    protected String description;

    public IncomeSource(String sourceId, double amount, String description) {
        this.sourceId = sourceId;
        this.amount = amount;
        this.description = description;
        this.taxAmount = 0;
    }

    public abstract double calculateTax();

    public String getSourceId() { return sourceId; }
    public double getAmount() { return amount; }
    public double getTaxAmount() { return taxAmount; }
    public String getDescription() { return description; }
}