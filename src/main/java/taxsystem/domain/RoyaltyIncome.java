package taxsystem.domain;

public class RoyaltyIncome extends IncomeSource {
    private String workTitle;
    private String workType;

    public RoyaltyIncome(String sourceId, double amount, String description,
                         String workTitle, String workType) {
        super(sourceId, amount, description);
        this.workTitle = workTitle;
        this.workType = workType;
    }

    @Override
    public String getIncomeType() {
        return "АВТОРСЬКА_ВИНАГОРОДА";
    }

    public String getWorkTitle() { return workTitle; }
    public void setWorkTitle(String workTitle) { this.workTitle = workTitle; }
    public String getWorkType() { return workType; }
    public void setWorkType(String workType) { this.workType = workType; }
}
