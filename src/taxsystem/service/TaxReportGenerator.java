package taxsystem.service;

import taxsystem.domain.Person;
import taxsystem.domain.IncomeSource;

public class TaxReportGenerator {
    private String reportFormat;
    private boolean includeDetails;

    public TaxReportGenerator() {
        this.reportFormat = "TEXT";
        this.includeDetails = true;
    }

    public String generateReport(Person person) {
        return "";
    }

    public String generateDetailedReport(Person person) {

        return "";
    }

    public String generateSummaryReport(Person person) {
        return "";
    }

    public void setReportFormat(String format) {
        this.reportFormat = format;
    }

    public void setIncludeDetails(boolean includeDetails) {
        this.includeDetails = includeDetails;
    }
}