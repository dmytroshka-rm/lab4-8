package taxsystem.repository;

public interface DataRepository {
    void exportTaxReport(String reportText, String filename);
}