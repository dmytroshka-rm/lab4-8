package taxsystem.repository;

import taxsystem.domain.Person;

import java.util.List;
import java.util.Optional;

public interface DataRepository {

    void save(Person person);

    Optional<Person> findById(String personId);

    List<Person> findAll();

    void update(Person person);

    void delete(String personId);

    void exportTaxReport(String reportText, String filename);
}
