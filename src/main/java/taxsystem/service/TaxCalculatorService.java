package taxsystem.service;

import taxsystem.domain.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class TaxCalculatorService {

    private static final Logger log = LogManager.getLogger(TaxCalculatorService.class);

    private Map<String, Double> taxRules;
    private int currentYear;
    private double nonTaxableMinimum;
    private Person currentPerson;

    public TaxCalculatorService() {
        log.info("Ініціалізація TaxCalculatorService...");
        this.taxRules = new HashMap<>();
        this.currentYear = 2025;
        this.nonTaxableMinimum = 1000.0;

        initializeTaxRules();
        this.currentPerson = null;

        log.info("TaxCalculatorService успішно створено. Неподатковий мінімум = {}", nonTaxableMinimum);
    }

    private void initializeTaxRules() {
        log.info("Завантаження податкових правил...");
        taxRules.put("ОПЛАТА_ПРАЦІ", 0.20);
        taxRules.put("ПОДАРУНОК", 0.15);
        taxRules.put("МАТЕРІАЛЬНА_ДОПОМОГА", 0.18);
    }

    public double getTaxRule(String incomeType) {
        double rate = taxRules.getOrDefault(incomeType, 0.18);
        log.debug("Отримання податкової ставки: {} = {}", incomeType, rate);
        return rate;
    }

    public double getNonTaxableMinimum() {
        return nonTaxableMinimum;
    }

    public Person getCurrentPerson() {
        return currentPerson;
    }

    public void setCurrentPerson(Person p) {
        if (p == null) {
            log.warn("setCurrentPerson викликано з null. Поточну особу не змінено.");
            return;
        }
        this.currentPerson = p;
        log.info("Поточну особу встановлено: {} {}", p.getFirstName(), p.getLastName());
    }

    public void addIncome(IncomeSource s) {
        if (currentPerson == null) {
            log.warn("Спроба додати дохід без створеної особи.");
            return;
        }
        if (s == null) {
            log.warn("Спроба додати null як дохід.");
            return;
        }

        currentPerson.getIncomeSources().add(s);
        log.info("Додано нове джерело доходу: {} сума = {}", s.getDescription(), s.getAmount());
    }

    public void addBenefit(TaxBenefit b) {
        if (currentPerson == null) {
            log.warn("Спроба додати пільгу без створеної особи.");
            return;
        }
        if (b == null) {
            log.warn("Спроба додати null пільгу.");
            return;
        }

        currentPerson.getTaxBenefits().add(b);
        log.info("Додано податкову пільгу: {} величина = {}", b.getDescription(), b.getAmount());
    }

    public void recalcTaxes() {
        if (currentPerson == null) {
            log.error("Перерахунок податків неможливий — особу не створено.");
            System.out.println("Спочатку створіть особу.");
            return;
        }

        log.info("Початок перерахунку податків для особи {}", currentPerson.getTaxId());

        for (IncomeSource s : currentPerson.getIncomeSources()) {
            try {
                s.calculateTax(this);
                log.debug("Розраховано податок для '{}': {}", s.getDescription(), s.getTaxAmount());
            } catch (Exception e) {
                log.error("Помилка розрахунку податку для '{}'", s.getDescription(), e);
            }
        }

        log.info("Перерахунок податків завершено.");
    }

    public double getTotalTaxBeforeBenefits() {
        if (currentPerson == null) {
            log.warn("Отримання суми податків до пільг — поточна особа відсутня.");
            return 0;
        }

        double total = 0;
        for (IncomeSource source : currentPerson.getIncomeSources()) {
            total += source.getTaxAmount();
        }

        log.info("Сума податків до пільг: {}", total);
        return total;
    }

    public double applyTaxBenefits(double taxAmount, List<TaxBenefit> benefits) {
        double result = taxAmount;
        if (benefits == null) return result;

        for (TaxBenefit b : benefits) {
            if (b != null && b.validateApplicability()) {
                log.info("Застосування пільги '{}'", b.getDescription());
                result = b.applyBenefit(result);
            }
        }

        log.info("Сума податку після застосування пільг: {}", result);
        return Math.max(0, result);
    }

    public double getTotalTaxAfterBenefits() {
        if (currentPerson == null) {
            log.warn("Спроба отримати податок після пільг — особа не встановлена.");
            return 0;
        }

        return applyTaxBenefits(
                getTotalTaxBeforeBenefits(),
                currentPerson.getTaxBenefits()
        );
    }

    public boolean validateTaxCalculation(Person person) {
        if (person == null) {
            log.warn("Валідація розрахунку: person = null");
            return false;
        }

        for (IncomeSource s : person.getIncomeSources()) {
            if (s.getAmount() < 0) {
                log.error("Некоректна сума доходу (від’ємна): {}", s.getAmount());
                return false;
            }
        }

        for (TaxBenefit b : person.getTaxBenefits()) {
            if (b.getAmount() < 0) {
                log.error("Некоректний розмір пільги (від’ємний): {}", b.getAmount());
                return false;
            }
        }

        log.info("Валідація податкових даних успішно пройдена.");
        return true;
    }
}
