package taxsystem.ui.command;

import taxsystem.repository.DataRepository;
import java.util.List;

public class LoadDataCommand implements Command {
    private DataRepository repository;

    public LoadDataCommand(DataRepository repository) {
        this.repository = repository;
    }

    @Override
    public void execute(List<String> parameters) {

    }

    @Override
    public String getDescription() {
        return "Завантажити дані про особу з файлу";
    }
}