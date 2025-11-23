package taxsystem.service;

import taxsystem.domain.*;

import java.util.stream.Collectors;

public class TaxReportGenerator {
    private String reportFormat;
    private boolean includeDetails;

    public TaxReportGenerator() {
        this.reportFormat = "DETAILED";
        this.includeDetails = true;
    }

    public String generateReport(Person person) {
        return includeDetails ? generateDetailedReport(person) : generateSummaryReport(person);
    }

    public String generateDetailedReport(Person person) {
        if (person == null) return "Немає даних про особу.";
        String header = String.format(
                "Податковий звіт для %s %s (ІПН: %s)\n",
                person.getFirstName(), person.getLastName(), person.getTaxId()
        );
        String incomes = person.getIncomeSources().isEmpty()
                ? "Джерела доходів відсутні.\n"
                : person.getIncomeSources().stream()
                .map(s -> String.format("- [%s] %s | сума: %.2f | податок: %.2f",
                        s.getClass().getSimpleName(), s.getDescription(), s.getAmount(), s.getTaxAmount()))
                .collect(Collectors.joining("\n")) + "\n";

        String benefits = person.getTaxBenefits().isEmpty()
                ? "Пільги відсутні.\n"
                : person.getTaxBenefits().stream()
                .map(b -> String.format("- [%s] %s | величина: %.2f | застосовується: %b",
                        b.getClass().getSimpleName(), b.getDescription(), b.getAmount(), b.isActive()))
                .collect(Collectors.joining("\n")) + "\n";

        double totalTax = person.getIncomeSources().stream().mapToDouble(IncomeSource::getTaxAmount).sum();
        return header + incomes + benefits + String.format("Сума податків до пільг: %.2f\n", totalTax);
    }

    public String generateSummaryReport(Person person) {
        if (person == null) return "Немає даних про особу.";
        double totalTax = person.getIncomeSources().stream().mapToDouble(IncomeSource::getTaxAmount).sum();
        return String.format("Платник: %s %s | Доходів: %d | Податок (без пільг): %.2f",
                person.getFirstName(), person.getLastName(),
                person.getIncomeSources().size(), totalTax);
    }

    public void setDetailedMode() {
        this.includeDetails = true;
        this.reportFormat = "DETAILED";
    }

    public void setSummaryMode() {
        this.includeDetails = false;
        this.reportFormat = "SUMMARY";
    }

    public String getReportFormat() {
        return reportFormat;
    }

    public boolean isIncludeDetails() {
        return includeDetails;
    }
}
