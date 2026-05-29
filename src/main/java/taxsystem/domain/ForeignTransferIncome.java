package taxsystem.domain;

public class ForeignTransferIncome extends IncomeSource {
    private String country;
    private String currency;

    public ForeignTransferIncome(String sourceId, double amount, String description,
                                 String country, String currency) {
        super(sourceId, amount, description);
        this.country = country;
        this.currency = currency;
    }

    @Override
    public String getIncomeType() {
        return "ПЕРЕКАЗ_З_ЗАКОРДОНУ";
    }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}
