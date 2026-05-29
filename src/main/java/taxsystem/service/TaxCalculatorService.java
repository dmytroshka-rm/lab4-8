package taxsystem.service;

import taxsystem.domain.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.*;
import java.util.stream.Collectors;

public class TaxCalculatorService {

    private static final Logger log = LogManager.getLogger(TaxCalculatorService.class);

    private final Map<String, Double> taxRules;
    private double nonTaxableMinimum;

    public TaxCalculatorService() {
        log.info("Ініціалізація TaxCalculatorService...");
        this.taxRules = new HashMap<>();
        this.nonTaxableMinimum = 1000.0;
        initializeTaxRules();
        log.info("TaxCalculatorService успішно створено. Неподатковий мінімум = {}", nonTaxableMinimum);
    }

    private void initializeTaxRules() {
        log.info("Завантаження податкових правил...");
        taxRules.put("ОПЛАТА_ПРАЦІ", 0.20);
        taxRules.put("ПОДАРУНОК", 0.15);
        taxRules.put("МАТЕРІАЛЬНА_ДОПОМОГА", 0.18);
        taxRules.put("АВТОРСЬКА_ВИНАГОРОДА", 0.18);
        taxRules.put("ПРОДАЖ_МАЙНА", 0.05);
        taxRules.put("ПРОДАЖ_МАЙНА_ПОВТОРНИЙ", 0.18);
        taxRules.put("ПЕРЕКАЗ_З_ЗАКОРДОНУ", 0.18);
    }

    public double getTaxRule(String incomeType) {
        double rate = taxRules.getOrDefault(incomeType, 0.18);
        log.debug("Отримання податкової ставки: {} = {}", incomeType, rate);
        return rate;
    }

    public double getNonTaxableMinimum() {
        return nonTaxableMinimum;
    }

    public double calculateTaxForIncome(IncomeSource income) {
        double tax = 0;

        if (income instanceof EmploymentIncome) {
            double base = Math.max(0, income.getAmount() - nonTaxableMinimum);
            tax = base * getTaxRule(income.getIncomeType());
        } else if (income instanceof GiftIncome gift) {
            if (gift.isCloseRelative()) {
                tax = 0.0;
            } else {
                tax = income.getAmount() * getTaxRule(income.getIncomeType());
            }
        } else if (income instanceof MaterialAid aid) {
            if (!aid.isTaxable()) {
                tax = 0.0;
            } else {
                tax = income.getAmount() * getTaxRule(income.getIncomeType());
            }
        } else if (income instanceof RoyaltyIncome) {
            tax = income.getAmount() * getTaxRule(income.getIncomeType());
        } else if (income instanceof PropertySaleIncome sale) {
            String ruleKey = sale.isFirstSalePerYear() ? "ПРОДАЖ_МАЙНА" : "ПРОДАЖ_МАЙНА_ПОВТОРНИЙ";
            tax = income.getAmount() * getTaxRule(ruleKey);
        } else if (income instanceof ForeignTransferIncome) {
            tax = income.getAmount() * getTaxRule(income.getIncomeType());
        }

        income.setTaxAmount(tax);
        log.debug("Розраховано податок для '{}': {}", income.getDescription(), tax);
        return tax;
    }

    public void recalcTaxes(Person person) {
        if (person == null) {
            log.error("Перерахунок податків неможливий — особу не задано.");
            return;
        }

        log.info("Початок перерахунку податків для особи {}", person.getTaxId());

        for (IncomeSource s : person.getIncomeSources()) {
            try {
                calculateTaxForIncome(s);
            } catch (Exception e) {
                log.error("Помилка розрахунку податку для '{}'", s.getDescription(), e);
            }
        }

        log.info("Перерахунок податків завершено.");
    }

    public double getTotalTaxBeforeBenefits(Person person) {
        if (person == null) {
            log.warn("Отримання суми податків до пільг — особа відсутня.");
            return 0;
        }

        double total = person.getIncomeSources().stream()
                .mapToDouble(IncomeSource::getTaxAmount)
                .sum();

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

    public double getTotalTaxAfterBenefits(Person person) {
        if (person == null) {
            log.warn("Спроба отримати податок після пільг — особа не задана.");
            return 0;
        }

        return applyTaxBenefits(
                getTotalTaxBeforeBenefits(person),
                person.getTaxBenefits()
        );
    }

    public boolean validateTaxCalculation(Person person) {
        if (person == null) {
            log.warn("Валідація розрахунку: person = null");
            return false;
        }

        for (IncomeSource s : person.getIncomeSources()) {
            if (s.getAmount() < 0) {
                log.error("Некоректна сума доходу (від'ємна): {}", s.getAmount());
                return false;
            }
        }

        for (TaxBenefit b : person.getTaxBenefits()) {
            if (b.getAmount() < 0) {
                log.error("Некоректний розмір пільги (від'ємний): {}", b.getAmount());
                return false;
            }
        }

        log.info("Валідація податкових даних успішно пройдена.");
        return true;
    }

    public List<IncomeSource> sortByTax(Person person, boolean ascending) {
        if (person == null) return Collections.emptyList();

        Comparator<IncomeSource> cmp = Comparator.comparingDouble(IncomeSource::getTaxAmount);
        if (!ascending) cmp = cmp.reversed();

        return person.getIncomeSources().stream()
                .sorted(cmp)
                .collect(Collectors.toList());
    }

    public List<IncomeSource> findByTaxRange(Person person, double minTax, double maxTax) {
        if (person == null) return Collections.emptyList();

        return person.getIncomeSources().stream()
                .filter(s -> s.getTaxAmount() >= minTax && s.getTaxAmount() <= maxTax)
                .collect(Collectors.toList());
    }
}
