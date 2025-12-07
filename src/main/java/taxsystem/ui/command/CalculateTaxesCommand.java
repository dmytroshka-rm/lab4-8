package taxsystem.ui.command;

import taxsystem.service.TaxCalculatorService;
import taxsystem.domain.Person;

import java.util.List;

public class CalculateTaxesCommand implements Command {
    private TaxCalculatorService taxService;

    public CalculateTaxesCommand(TaxCalculatorService taxService) {
        this.taxService = taxService;
    }

    @Override
    public void execute(List<String> parameters) {
        Person p = taxService.getCurrentPerson();
        if (!taxService.validateTaxCalculation(p)) {
            System.out.println("Дані некоректні.Перевірте суми доходів/пільг.");
            return;
        }
        taxService.recalcTaxes();
        double before = taxService.getTotalTaxBeforeBenefits();
        double after = taxService.getTotalTaxAfterBenefits();
        System.out.printf("Податок до пільг: %.2f%n", before);
        System.out.printf("Податок після пільг: %.2f%n", after);
    }

    @Override
    public String getDescription() {
        return "Розрахувати податки для поточної особи";
    }
}
