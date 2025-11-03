package taxsystem.repository;

import taxsystem.domain.Person;

public interface DataRepository {
    void savePerson(Person person, String filename);
    Person loadPerson(String filename);
    void exportTaxReport(Person person, String filename);
}
