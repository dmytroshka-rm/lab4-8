package taxsystem.service;

import taxsystem.domain.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.Collectors;

public class TaxReportGenerator {

    private static final Logger log = LogManager.getLogger(TaxReportGenerator.class);

    private String reportFormat;
    private boolean includeDetails;

    public TaxReportGenerator() {
        this.reportFormat = "DETAILED";
        this.includeDetails = true;
        log.info("TaxReportGenerator ініціалізовано. Режим = детальний.");
    }

    public String generateReport(Person person) {
        if (person == null) {
            log.warn("Спроба генерації звіту при person = null");
            return "Немає даних про особу.";
        }

        log.info("Формування {} звіту для особи {} {}",
                includeDetails ? "детального" : "короткого",
                person.getFirstName(), person.getLastName());

        return includeDetails ? generateDetailedReport(person) : generateSummaryReport(person);
    }

    public String generateDetailedReport(Person person) {
        if (person == null) {
            log.warn("generateDetailedReport: person = null");
            return "Немає даних про особу.";
        }

        log.debug("Побудова детального звіту...");

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

        double totalTax = person.getIncomeSources()
                .stream()
                .mapToDouble(IncomeSource::getTaxAmount)
                .sum();

        log.info("Детальний звіт сформовано. Податок до пільг = {}", totalTax);

        return header + incomes + benefits + String.format("Сума податків до пільг: %.2f\n", totalTax);
    }

    public String generateSummaryReport(Person person) {
        if (person == null) {
            log.warn("generateSummaryReport: person = null");
            return "Немає даних про особу.";
        }

        log.debug("Побудова короткого звіту...");

        double totalTax = person.getIncomeSources()
                .stream()
                .mapToDouble(IncomeSource::getTaxAmount)
                .sum();

        log.info("Короткий звіт сформовано. Податок до пільг = {}", totalTax);

        return String.format("Платник: %s %s | Доходів: %d | Податок (без пільг): %.2f",
                person.getFirstName(), person.getLastName(),
                person.getIncomeSources().size(), totalTax);
    }

    public void setDetailedMode() {
        this.includeDetails = true;
        this.reportFormat = "DETAILED";
        log.info("Режим звіту змінено на детальний.");
    }

    public void setSummaryMode() {
        this.includeDetails = false;
        this.reportFormat = "SUMMARY";
        log.info("Режим звіту змінено на короткий.");
    }

    public String getReportFormat() {
        return reportFormat;
    }

    public boolean isIncludeDetails() {
        return includeDetails;
    }
}
