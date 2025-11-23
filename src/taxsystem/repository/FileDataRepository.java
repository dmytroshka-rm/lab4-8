package taxsystem.repository;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileDataRepository implements DataRepository {
    private String dataDirectory = "data/";
    private String fileExtension = ".txt";

    @Override
    public void exportTaxReport(String reportText, String filename) {
        if (reportText == null || reportText.isBlank()) {
            System.err.println("Неможливо зберегти порожній звіт.");
            return;
        }

        File f = ensureFile(filename);
        try (PrintWriter out = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8))) {

            out.print(reportText);
            System.out.println("Звіт експортовано: " + f.getAbsolutePath());

        } catch (IOException e) {
            System.err.println("Помилка експорту звіту: " + e.getMessage());
        }
    }

    private File ensureFile(String filename) {
        if (filename == null || filename.isBlank()) {
            filename = "report";
        }
        if (!filename.endsWith(fileExtension)) {
            filename += fileExtension;
        }

        File dir = new File(dataDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        return new File(dir, filename);
    }
}
