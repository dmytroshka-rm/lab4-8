package taxsystem.ui.command;

import taxsystem.service.TaxCalculatorService;
import java.util.List;

public class AddIncomeCommand implements Command {
    private TaxCalculatorService taxService;

    public AddIncomeCommand(TaxCalculatorService taxService) {
        this.taxService = taxService;
    }

    @Override
    public void execute(List<String> parameters) {

    }

    @Override
    public String getDescription() {
        return "Додати нове джерело доходу";
    }
}