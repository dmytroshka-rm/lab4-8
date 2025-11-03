package taxsystem.ui.command;

import taxsystem.service.TaxCalculatorService;
import java.util.List;

public class AddBenefitCommand implements Command {
    private TaxCalculatorService taxService;

    public AddBenefitCommand(TaxCalculatorService taxService) {
        this.taxService = taxService;
    }

    @Override
    public void execute(List<String> parameters) {

    }

    @Override
    public String getDescription() {
        return "Додати нову податкову пільгу";
    }
}