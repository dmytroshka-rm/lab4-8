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
    public String getIncomeType() {
        return "ОПЛАТА_ПРАЦІ";
    }

    public String getEmployerName() { return employerName; }
    public void setEmployerName(String employerName) { this.employerName = employerName; }
    public boolean isMainJob() { return isMainJob; }
    public void setMainJob(boolean mainJob) { isMainJob = mainJob; }
}
