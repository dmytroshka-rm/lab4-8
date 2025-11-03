package taxsystem.ui.command;

import taxsystem.service.TaxCalculatorService;
import java.util.List;

public class SortTaxesCommand implements Command {
    private TaxCalculatorService taxService;

    public SortTaxesCommand(TaxCalculatorService taxService) {
        this.taxService = taxService;
    }

    @Override
    public void execute(List<String> parameters) {
    }

    @Override
    public String getDescription() {
        return "Відсортувати податки за сумою";
    }
}