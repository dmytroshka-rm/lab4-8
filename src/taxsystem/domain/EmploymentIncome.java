package taxsystem.domain;

public class EmploymentIncome extends IncomeSource {
    private String employerName;
    private boolean isMainJob;

    public EmploymentIncome(String sourceId, double amount, String description, String employerName, boolean isMainJob) {
        super(sourceId, amount, description);
        this.employerName = employerName;
        this.isMainJob = isMainJob;
    }

    @Override
    public double calculateTax() {
        this.taxAmount = amount * 0.18;
        return taxAmount;
    }

    public String getEmployerName() { return employerName; }
    public boolean isMainJob() { return isMainJob; }
}
