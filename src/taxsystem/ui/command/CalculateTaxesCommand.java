package taxsystem.ui.command;

import taxsystem.service.TaxCalculatorService;
import java.util.List;

public class CalculateTaxesCommand implements Command {
    private TaxCalculatorService taxService;

    public CalculateTaxesCommand(TaxCalculatorService taxService) {
        this.taxService = taxService;
    }

    @Override
    public void execute(List<String> parameters) {

    }

    @Override
    public String getDescription() {
        return "Розрахувати податки для поточної особи";
    }
}