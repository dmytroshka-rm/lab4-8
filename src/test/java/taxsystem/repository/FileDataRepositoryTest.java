package taxsystem.repository;

import org.junit.jupiter.api.*;
import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class FileDataRepositoryTest {

    private FileDataRepository repo;

    @BeforeEach
    void setup() {
        repo = new FileDataRepository();

        // очищаємо каталог data перед тестами
        File dir = new File("data");
        if (dir.exists()) {
            for (File f : dir.listFiles()) f.delete();
        }
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
    void testDirectoryCreatedAutomatically() {
        File dir = new File("data");
        if (dir.exists()) {
            for (File f : dir.listFiles()) f.delete();
            dir.delete();
        }
        assertFalse(dir.exists());

        repo.exportTaxReport("test", "auto");

        assertTrue(dir.exists());
        assertTrue(new File("data/auto.txt").exists());
    }

    @Test
    void testIOExceptionHandledGracefully() throws Exception {
        // Створюємо файл, який неможливо перезаписати
        File locked = new File("data/locked.txt");
        locked.getParentFile().mkdirs();
        locked.createNewFile();
        locked.setWritable(false);

        repo.exportTaxReport("FAIL", "locked.txt");

        // тест успішний, якщо немає вильоту виключення
        assertTrue(locked.exists());

        locked.setWritable(true);
        locked.delete();
    }
}
