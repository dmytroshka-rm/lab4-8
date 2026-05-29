package taxsystem.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PersonTest {

    @Test
    void testConstructorAndGetters() {
        Person person = new Person("P1", "Ivan", "Ivanov", "12345");

        assertEquals("P1", person.getPersonId());
        assertEquals("Ivan", person.getFirstName());
        assertEquals("Ivanov", person.getLastName());
        assertEquals("12345", person.getTaxId());
        assertNotNull(person.getIncomeSources());
        assertNotNull(person.getTaxBenefits());
        assertTrue(person.getIncomeSources().isEmpty());
        assertTrue(person.getTaxBenefits().isEmpty());
    }

    @Test
    void testSetters() {
        Person person = new Person("P1", "Ivan", "Ivanov", "12345");

        person.setPersonId("P2");
        assertEquals("P2", person.getPersonId());

        person.setFirstName("Petro");
        assertEquals("Petro", person.getFirstName());

        person.setLastName("Petrov");
        assertEquals("Petrov", person.getLastName());

        person.setTaxId("99999");
        assertEquals("99999", person.getTaxId());
    }

    @Test
    void testListsAreMutable() {
        Person person = new Person("P1", "Ivan", "Ivanov", "12345");

        person.getIncomeSources().add(
                new MaterialAid("A1", 1000, "aid", "одноразова", false)
        );
        person.getTaxBenefits().add(
                new ChildBenefit("B1", 500, "child", 1)
        );

        assertEquals(1, person.getIncomeSources().size());
        assertEquals(1, person.getTaxBenefits().size());
    }
}
