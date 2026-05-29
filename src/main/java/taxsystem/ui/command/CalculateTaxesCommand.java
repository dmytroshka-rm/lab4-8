package taxsystem.ui.command;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import taxsystem.service.PersonService;
import taxsystem.service.TaxCalculatorService;
import taxsystem.domain.Person;

import java.util.List;

public class CalculateTaxesCommand implements Command {
    private static final Logger log = LogManager.getLogger(CalculateTaxesCommand.class);
    private final PersonService personService;
    private final TaxCalculatorService taxService;

    public CalculateTaxesCommand(PersonService personService, TaxCalculatorService taxService) {
        this.personService = personService;
        this.taxService = taxService;
    }

    @Override
    public void execute(List<String> parameters) {
        Person p = personService.getCurrentPerson();
        if (p == null) {
            log.warn("Команда CalculateTaxes: особу не задано.");
            System.out.println("Спочатку створіть особу (команда create_person).");
            return;
        }

        if (!taxService.validateTaxCalculation(p)) {
            log.warn("Команда CalculateTaxes: валідація не пройдена для особи {}", p.getPersonId());
            System.out.println("Дані некоректні. Перевірте суми доходів/пільг.");
            return;
        }

        taxService.recalcTaxes(p);
        double before = taxService.getTotalTaxBeforeBenefits(p);
        double after = taxService.getTotalTaxAfterBenefits(p);
        log.info("Розраховано податки для особи {}: до пільг = {}, після пільг = {}", p.getPersonId(), before, after);
        System.out.printf("Податок до пільг: %.2f%n", before);
        System.out.printf("Податок після пільг: %.2f%n", after);
    }

    @Override
    public String getDescription() {
        return "Розрахувати податки для поточної особи";
    }
}
