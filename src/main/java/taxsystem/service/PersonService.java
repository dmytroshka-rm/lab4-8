package taxsystem.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import taxsystem.domain.IncomeSource;
import taxsystem.domain.Person;
import taxsystem.domain.TaxBenefit;
import taxsystem.repository.DataRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PersonService {

    private static final Logger log = LogManager.getLogger(PersonService.class);

    private final DataRepository repository;
    private Person currentPerson;

    public PersonService(DataRepository repository) {
        this.repository = repository;
    }

    public Person createPerson(String firstName, String lastName, String taxId) {
        String personId = UUID.randomUUID().toString().substring(0, 8);
        Person person = new Person(personId, firstName, lastName, taxId);
        repository.save(person);
        this.currentPerson = person;
        log.info("Створено та збережено нову особу: {} {} (ID: {})", firstName, lastName, personId);
        return person;
    }

    public void setCurrentPerson(Person person) {
        if (person == null) {
            log.warn("setCurrentPerson викликано з null.");
            return;
        }
        this.currentPerson = person;
        log.info("Поточну особу встановлено: {} {}", person.getFirstName(), person.getLastName());
    }

    public Person getCurrentPerson() {
        return currentPerson;
    }

    public Optional<Person> findById(String personId) {
        return repository.findById(personId);
    }

    public List<Person> findAll() {
        return repository.findAll();
    }

    public void deletePerson(String personId) {
        if (currentPerson != null && currentPerson.getPersonId().equals(personId)) {
            currentPerson = null;
        }
        repository.delete(personId);
        log.info("Видалено особу з ID: {}", personId);
    }

    public void addIncome(IncomeSource income) {
        if (currentPerson == null) {
            log.warn("Спроба додати дохід без створеної особи.");
            return;
        }
        if (income == null) {
            log.warn("Спроба додати null як дохід.");
            return;
        }
        currentPerson.getIncomeSources().add(income);
        repository.update(currentPerson);
        log.info("Додано джерело доходу: {} сума = {}", income.getDescription(), income.getAmount());
    }

    public void addBenefit(TaxBenefit benefit) {
        if (currentPerson == null) {
            log.warn("Спроба додати пільгу без створеної особи.");
            return;
        }
        if (benefit == null) {
            log.warn("Спроба додати null пільгу.");
            return;
        }
        currentPerson.getTaxBenefits().add(benefit);
        repository.update(currentPerson);
        log.info("Додано податкову пільгу: {} величина = {}", benefit.getDescription(), benefit.getAmount());
    }

    public void removeIncome(String sourceId) {
        if (currentPerson == null) return;
        currentPerson.getIncomeSources().removeIf(s -> s.getSourceId().equals(sourceId));
        repository.update(currentPerson);
        log.info("Видалено джерело доходу з ID: {}", sourceId);
    }

    public void removeBenefit(String benefitId) {
        if (currentPerson == null) return;
        currentPerson.getTaxBenefits().removeIf(b -> b.getBenefitId().equals(benefitId));
        repository.update(currentPerson);
        log.info("Видалено пільгу з ID: {}", benefitId);
    }
}
