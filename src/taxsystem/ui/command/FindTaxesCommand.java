package taxsystem.ui.command;

import taxsystem.service.TaxCalculatorService;
import java.util.List;

public class FindTaxesCommand implements Command {
    private TaxCalculatorService taxService;

    public FindTaxesCommand(TaxCalculatorService taxService) {
        this.taxService = taxService;
    }

    @Override
    public void execute(List<String> parameters) {
    }

    @Override
    public String getDescription() {
        return "Знайти податки у вказаному діапазоні сум";
    }
}