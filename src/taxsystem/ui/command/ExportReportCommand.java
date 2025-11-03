package taxsystem.ui.command;

import taxsystem.service.TaxReportGenerator;
import java.util.List;

public class ExportReportCommand implements Command {
    private TaxReportGenerator reportGenerator;

    public ExportReportCommand(TaxReportGenerator reportGenerator) {
        this.reportGenerator = reportGenerator;
    }

    @Override
    public void execute(List<String> parameters) {

    }

    @Override
    public String getDescription() {
        return "Експортувати податковий звіт у файл";
    }
}