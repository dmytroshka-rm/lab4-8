package taxsystem.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import taxsystem.domain.Person;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FileDataRepository implements DataRepository {

    private static final Logger log = LogManager.getLogger(FileDataRepository.class);

    private final Map<String, Person> storage = new LinkedHashMap<>();
    private final String dataDirectory = "data/";
    private final String fileExtension = ".txt";

    @Override
    public void save(Person person) {
        if (person == null) {
            log.warn("Спроба зберегти null особу.");
            return;
        }
        storage.put(person.getPersonId(), person);
        log.info("Збережено особу: {} {} (ID: {})", person.getFirstName(), person.getLastName(), person.getPersonId());
    }

    @Override
    public Optional<Person> findById(String personId) {
        return Optional.ofNullable(storage.get(personId));
    }

    @Override
    public List<Person> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void update(Person person) {
        if (person == null || !storage.containsKey(person.getPersonId())) {
            log.warn("Спроба оновити неіснуючу або null особу.");
            return;
        }
        storage.put(person.getPersonId(), person);
        log.info("Оновлено особу: {} (ID: {})", person.getLastName(), person.getPersonId());
    }

    @Override
    public void delete(String personId) {
        Person removed = storage.remove(personId);
        if (removed != null) {
            log.info("Видалено особу: {} {} (ID: {})", removed.getFirstName(), removed.getLastName(), personId);
        } else {
            log.warn("Спроба видалити неіснуючу особу з ID: {}", personId);
        }
    }

    @Override
    public void exportTaxReport(String reportText, String filename) {
        if (reportText == null || reportText.isBlank()) {
            log.warn("Спроба зберегти порожній звіт. Операцію скасовано.");
            return;
        }

        File f = ensureFile(filename);
        log.info("Експорт податкового звіту у файл '{}'", f.getAbsolutePath());

        try (PrintWriter out = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8))) {
            out.print(reportText);
            log.info("Звіт успішно експортовано у '{}'", f.getAbsolutePath());
        } catch (IOException e) {
            log.error("Помилка під час експорту звіту у файл '{}'", f.getAbsolutePath(), e);
        }
    }

    private File ensureFile(String filename) {
        if (filename == null || filename.isBlank()) {
            log.warn("Ім'я файлу не задане. Використано дефолтне ім'я 'report.txt'.");
            filename = "report";
        }
        if (!filename.endsWith(fileExtension)) {
            filename += fileExtension;
        }

        File dir = new File(dataDirectory);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (created) {
                log.info("Створено директорію '{}'", dir.getAbsolutePath());
            } else {
                log.warn("Не вдалося створити директорію '{}'", dir.getAbsolutePath());
            }
        }

        return new File(dir, filename);
    }
}
