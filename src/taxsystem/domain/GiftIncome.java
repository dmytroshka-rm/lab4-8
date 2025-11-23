package taxsystem.domain;

import taxsystem.service.TaxCalculatorService;

import java.util.Set;

public class GiftIncome extends IncomeSource {
    private String donorName;
    private String relationship;

    public GiftIncome(String sourceId, double amount, String description, String donorName, String relationship) {
        super(sourceId, amount, description);
        this.donorName = donorName;
        this.relationship = relationship;
    }

    @Override
    public double calculateTax(TaxCalculatorService tcs) {
        boolean isCloseRelative = false;
        if (relationship != null) {
            String rel = relationship.trim().toLowerCase();
            Set<String> close = Set.of("близька родина", "подружжя", "батько", "мати", "дитина", "син", "донька", "дочка", "брат", "сестра");
            isCloseRelative = close.contains(rel);
        }
        if (isCloseRelative) {
            this.taxAmount = 0.0;
        } else {
            double rate = tcs.getTaxRule("ПОДАРУНОК");
            this.taxAmount = amount * rate;
        }
        return taxAmount;
    }

}
