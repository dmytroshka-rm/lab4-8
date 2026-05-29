package taxsystem.ui.command;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import taxsystem.service.TaxReportGenerator;

import java.util.List;

public class SetReportModeCommand implements Command {
    private static final Logger log = LogManager.getLogger(SetReportModeCommand.class);
    private final TaxReportGenerator reportGenerator;

    public SetReportModeCommand(TaxReportGenerator reportGenerator) {
        this.reportGenerator = reportGenerator;
    }

    @Override
    public void execute(List<String> parameters) {
        if (parameters.isEmpty()) {
            log.warn("Команда SetReportMode: режим не вказано.");
            System.out.println("Використання: report_mode <detailed|summary>");
            return;
        }

        String mode = parameters.get(0).toLowerCase();

        switch (mode) {
            case "detailed" -> {
                reportGenerator.setDetailedMode();
                log.info("Режим звіту змінено на: DETAILED");
                System.out.println("Режим звіту встановлено: детальний.");
            }
            case "summary" -> {
                reportGenerator.setSummaryMode();
                log.info("Режим звіту змінено на: SUMMARY");
                System.out.println("Режим звіту встановлено: короткий.");
            }
            default -> {
                log.warn("Команда SetReportMode: невідомий режим '{}'", mode);
                System.out.println("Невідомий режим: " + mode + ". Можливі: detailed, summary.");
            }
        }
    }

    @Override
    public String getDescription() {
        return "Змінити режим формування звіту (детальний/короткий)";
    }
}
