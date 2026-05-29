package taxsystem.domain;

public class PropertySaleIncome extends IncomeSource {
    private String propertyType;
    private boolean isFirstSalePerYear;

    public PropertySaleIncome(String sourceId, double amount, String description,
                              String propertyType, boolean isFirstSalePerYear) {
        super(sourceId, amount, description);
        this.propertyType = propertyType;
        this.isFirstSalePerYear = isFirstSalePerYear;
    }

    @Override
    public String getIncomeType() {
        return "ПРОДАЖ_МАЙНА";
    }

    public String getPropertyType() { return propertyType; }
    public void setPropertyType(String propertyType) { this.propertyType = propertyType; }
    public boolean isFirstSalePerYear() { return isFirstSalePerYear; }
    public void setFirstSalePerYear(boolean firstSalePerYear) { isFirstSalePerYear = firstSalePerYear; }
}
