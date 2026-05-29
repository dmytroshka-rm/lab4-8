package taxsystem.repository;

import org.junit.jupiter.api.*;
import taxsystem.domain.Person;

import java.io.*;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class FileDataRepositoryTest {

    private FileDataRepository repo;

    @BeforeEach
    void setup() {
        repo = new FileDataRepository();

        File dir = new File("data");
        if (dir.exists()) {
            for (File f : dir.listFiles()) f.delete();
        }
    }

    @Test
    void testSaveAndFindById() {
        Person person = new Person("P1", "Ivan", "Ivanov", "12345");
        repo.save(person);

        Optional<Person> found = repo.findById("P1");
        assertTrue(found.isPresent());
        assertEquals("Ivan", found.get().getFirstName());
    }

    @Test
    void testFindByIdNotFound() {
        Optional<Person> found = repo.findById("NONE");
        assertFalse(found.isPresent());
    }

    @Test
    void testFindAll() {
        repo.save(new Person("P1", "Ivan", "Ivanov", "111"));
        repo.save(new Person("P2", "Petro", "Petrov", "222"));

        List<Person> all = repo.findAll();
        assertEquals(2, all.size());
    }

    @Test
    void testUpdate() {
        Person person = new Person("P1", "Ivan", "Ivanov", "12345");
        repo.save(person);

        person.setFirstName("Petro");
        repo.update(person);

        Optional<Person> found = repo.findById("P1");
        assertTrue(found.isPresent());
        assertEquals("Petro", found.get().getFirstName());
    }

    @Test
    void testUpdateNonExistent() {
        Person person = new Person("NONE", "Ivan", "Ivanov", "12345");
        repo.update(person);

        assertFalse(repo.findById("NONE").isPresent());
    }

    @Test
    void testDelete() {
        repo.save(new Person("P1", "Ivan", "Ivanov", "111"));
        repo.delete("P1");

        assertFalse(repo.findById("P1").isPresent());
        assertTrue(repo.findAll().isEmpty());
    }

    @Test
    void testDeleteNonExistent() {
        repo.delete("NONE");
        assertTrue(repo.findAll().isEmpty());
    }

    @Test
    void testSaveNullIgnored() {
        repo.save(null);
        assertTrue(repo.findAll().isEmpty());
    }

    @Test
    void testExportCreatesFile() {
        repo.exportTaxReport("hello world", "test_file");

        File file = new File("data/test_file.txt");
        assertTrue(file.exists());
        assertTrue(file.length() > 0);
        file.delete();
    }

    @Test
    void testExportEmptyReportDoesNotCreateFile() {
        repo.exportTaxReport("", "empty_file");

        File file = new File("data/empty_file.txt");
        assertFalse(file.exists());
    }

    @Test
    void testExportNullReportDoesNotCreateFile() {
        repo.exportTaxReport(null, "null_report");

        File file = new File("data/null_report.txt");
        assertFalse(file.exists());
    }

    @Test
    void testEnsureFileAddsExtension() {
        repo.exportTaxReport("abc", "noext");

        File file = new File("data/noext.txt");
        assertTrue(file.exists());
        file.delete();
    }

    @Test
    void testEnsureFileUsesDefaultName() {
        repo.exportTaxReport("abc", "");

        File file = new File("data/report.txt");
        assertTrue(file.exists());
        file.delete();
    }

    @Test
    void testUpdateNullIgnored() {
        repo.update(null);
        assertTrue(repo.findAll().isEmpty());
    }

    @Test
    void testExportWithNullFilename() {
        repo.exportTaxReport("report content", null);
        File file = new File("data/report.txt");
        assertTrue(file.exists());
        file.delete();
    }

    @Test
    void testExportWithTxtExtension() {
        repo.exportTaxReport("data", "already.txt");
        File file = new File("data/already.txt");
        assertTrue(file.exists());
        file.delete();
    }

    @Test
    void testSaveOverwrites() {
        Person p = new Person("P1", "Ivan", "Ivanov", "111");
        repo.save(p);
        p.setFirstName("Petro");
        repo.save(p);
        assertEquals("Petro", repo.findById("P1").get().getFirstName());
        assertEquals(1, repo.findAll().size());
    }

    @Test
    void testExportCreatesDirectoryIfMissing() {
        // Видаляємо директорію data/ щоб перевірити mkdirs()
        File dir = new File("data");
        if (dir.exists()) {
            for (File f : dir.listFiles()) f.delete();
            dir.delete();
        }
        assertFalse(dir.exists(), "Директорія data має бути видалена перед тестом");

        repo.exportTaxReport("test content", "mkdirs_test");

        File file = new File("data/mkdirs_test.txt");
        assertTrue(file.exists(), "Файл має бути створений разом з директорією");
        file.delete();
    }
}
