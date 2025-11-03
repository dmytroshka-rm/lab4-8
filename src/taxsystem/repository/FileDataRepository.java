package taxsystem.repository;

import taxsystem.domain.Person;

public class FileDataRepository implements DataRepository {
    private String dataDirectory;
    private String fileExtension;

    public FileDataRepository() {
        this.dataDirectory = "data/";
        this.fileExtension = ".txt";
    }

    @Override
    public void savePerson(Person person, String filename) {

    }

    @Override
    public Person loadPerson(String filename) {
        return null;
    }

    @Override
    public void exportTaxReport(Person person, String filename) {

    }

    public void setDataDirectory(String dataDirectory) {
        this.dataDirectory = dataDirectory;
    }
}