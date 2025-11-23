package taxsystem.ui.command;

import taxsystem.service.TaxReportGenerator;

import java.util.List;

public class SetReportModeCommand implements Command {
    private final TaxReportGenerator reportGenerator;

    public SetReportModeCommand(TaxReportGenerator reportGenerator) {
        this.reportGenerator = reportGenerator;
    }

    @Override
    public void execute(List<String> parameters) {
        if (parameters.isEmpty()) {
            System.out.println("Використання: report_mode <detailed|summary>");
            return;
        }

        String mode = parameters.get(0).toLowerCase();

        switch (mode) {
            case "detailed" -> {
                reportGenerator.setDetailedMode();
                System.out.println("Режим звіту встановлено: детальний.");
            }
            case "summary" -> {
                reportGenerator.setSummaryMode();
                System.out.println("Режим звіту встановлено: короткий.");
            }
            default -> System.out.println("Невідомий режим: " + mode + ". Можливі: detailed, summary.");
        }
    }

    @Override
    public String getDescription() {
        return "Змінити режим формування звіту (детальний/короткий)";
    }
}
