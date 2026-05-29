package taxsystem.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taxsystem.domain.*;
import taxsystem.repository.FileDataRepository;

import static org.junit.jupiter.api.Assertions.*;

class PersonServiceTest {

    private PersonService personService;

    @BeforeEach
    void setup() {
        personService = new PersonService(new FileDataRepository());
    }

    @Test
    void testCreatePerson() {
        Person p = personService.createPerson("Ivan", "Ivanov", "12345");

        assertNotNull(p);
        assertNotNull(p.getPersonId());
        assertEquals("Ivan", p.getFirstName());
        assertSame(p, personService.getCurrentPerson());
    }

    @Test
    void testSetCurrentPerson() {
        Person p = new Person("P1", "Ivan", "Ivanov", "12345");
        personService.setCurrentPerson(p);
        assertSame(p, personService.getCurrentPerson());
    }

    @Test
    void testSetCurrentPersonNull() {
        personService.createPerson("Ivan", "Ivanov", "12345");
        personService.setCurrentPerson(null);
        assertNotNull(personService.getCurrentPerson());
    }

    @Test
    void testFindById() {
        Person p = personService.createPerson("Ivan", "Ivanov", "12345");
        assertTrue(personService.findById(p.getPersonId()).isPresent());
        assertFalse(personService.findById("NONE").isPresent());
    }

    @Test
    void testFindAll() {
        personService.createPerson("Ivan", "Ivanov", "111");
        personService.createPerson("Petro", "Petrov", "222");

        assertEquals(2, personService.findAll().size());
    }

    @Test
    void testDeletePerson() {
        Person p = personService.createPerson("Ivan", "Ivanov", "12345");
        personService.deletePerson(p.getPersonId());

        assertNull(personService.getCurrentPerson());
        assertFalse(personService.findById(p.getPersonId()).isPresent());
    }

    @Test
    void testAddIncome() {
        personService.createPerson("Ivan", "Ivanov", "12345");

        EmploymentIncome income = new EmploymentIncome("I1", 5000, "job", "Company", true);
        personService.addIncome(income);

        assertEquals(1, personService.getCurrentPerson().getIncomeSources().size());
    }

    @Test
    void testAddIncomeNullIgnored() {
        personService.createPerson("Ivan", "Ivanov", "12345");
        personService.addIncome(null);
        assertEquals(0, personService.getCurrentPerson().getIncomeSources().size());
    }

    @Test
    void testAddIncomeNoPersonIgnored() {
        personService.addIncome(new EmploymentIncome("I1", 5000, "job", "Company", true));
    }

    @Test
    void testAddBenefit() {
        personService.createPerson("Ivan", "Ivanov", "12345");

        ChildBenefit benefit = new ChildBenefit("B1", 500, "child", 2);
        personService.addBenefit(benefit);

        assertEquals(1, personService.getCurrentPerson().getTaxBenefits().size());
    }

    @Test
    void testAddBenefitNullIgnored() {
        personService.createPerson("Ivan", "Ivanov", "12345");
        personService.addBenefit(null);
        assertEquals(0, personService.getCurrentPerson().getTaxBenefits().size());
    }

    @Test
    void testRemoveIncome() {
        personService.createPerson("Ivan", "Ivanov", "12345");
        personService.addIncome(new EmploymentIncome("I1", 5000, "job", "Company", true));

        personService.removeIncome("I1");
        assertEquals(0, personService.getCurrentPerson().getIncomeSources().size());
    }

    @Test
    void testRemoveBenefit() {
        personService.createPerson("Ivan", "Ivanov", "12345");
        personService.addBenefit(new ChildBenefit("B1", 500, "child", 2));

        personService.removeBenefit("B1");
        assertEquals(0, personService.getCurrentPerson().getTaxBenefits().size());
    }

    @Test
    void testRemoveIncomeNoPersonIgnored() {
        personService.removeIncome("I1");
    }

    @Test
    void testRemoveBenefitNoPersonIgnored() {
        personService.removeBenefit("B1");
    }

    @Test
    void testAddBenefitNoPersonIgnored() {
        personService.addBenefit(new ChildBenefit("B1", 500, "child", 2));
    }

    @Test
    void testDeletePersonNotCurrent() {
        Person p1 = personService.createPerson("Ivan", "Ivanov", "111");
        Person p2 = personService.createPerson("Petro", "Petrov", "222");
        personService.deletePerson(p1.getPersonId());
        assertNotNull(personService.getCurrentPerson());
    }
}
