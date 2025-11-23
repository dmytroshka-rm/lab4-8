package taxsystem.ui.command;

import taxsystem.service.TaxCalculatorService;
import taxsystem.domain.IncomeSource;

import java.util.Comparator;
import java.util.List;

public class SortTaxesCommand implements Command {
    private TaxCalculatorService taxService;

    public SortTaxesCommand(TaxCalculatorService taxService) {
        this.taxService = taxService;
    }

    @Override
    public void execute(List<String> parameters) {
        String dir = parameters.isEmpty() ? "asc" : parameters.get(0).toLowerCase();
        Comparator<IncomeSource> cmp = Comparator.comparingDouble(IncomeSource::getTaxAmount);
        if ("desc".equals(dir)) cmp = cmp.reversed();
        taxService.recalcTaxes();
        taxService.getCurrentPerson().getIncomeSources().stream()
                .sorted(cmp)
                .forEach(s -> System.out.printf("%s | %s | сума: %.2f | податок: %.2f%n",
                        s.getClass().getSimpleName(), s.getDescription(), s.getAmount(), s.getTaxAmount()));
    }

    @Override
    public String getDescription() {
        return "Відсортувати податки за сумою";
    }
}
