package taxsystem.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileDataRepository implements DataRepository {

    private static final Logger log = LogManager.getLogger(FileDataRepository.class);

    private String dataDirectory = "data/";
    private String fileExtension = ".txt";

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
            // ERROR буде відправлено на Email згідно log4j2.xml
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

        File file = new File(dir, filename);
        log.debug("Повний шлях файлу: {}", file.getAbsolutePath());
        return file;
    }
}
