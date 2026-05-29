package taxsystem.ui;

import javafx.scene.control.*;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;
import taxsystem.domain.*;
import taxsystem.repository.DatabaseManager;
import taxsystem.repository.SqliteDataRepository;
import taxsystem.repository.DataRepository;
import taxsystem.service.PersonService;
import taxsystem.service.TaxCalculatorService;
import taxsystem.service.TaxReportGenerator;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
class MainControllerTest {

    private Stage stage;
    private PersonService personService;
    private DataRepository repository;

    @Start
    void start(Stage stage) throws Exception {
        this.stage = stage;
        File tmpDb = File.createTempFile("maintest_", ".db");
        tmpDb.deleteOnExit();
        DatabaseManager dbManager = new DatabaseManager(tmpDb.getAbsolutePath());
        repository = new SqliteDataRepository(dbManager);
        personService = new PersonService(repository);

        MainController c = new MainController(stage, personService,
                new TaxCalculatorService(), new TaxReportGenerator(), repository);
        c.setCurrentUser(new User("tester", "", ""));
        c.show();
    }

    private void closeDialogs(FxRobot robot) {
        WaitForAsyncUtils.waitForFxEvents();
        robot.interact(() -> {
            try {
                var toClose = new java.util.ArrayList<>(javafx.stage.Window.getWindows());
                for (var w : toClose) {
                    if (w != stage && w.isShowing()) w.hide();
                }
            } catch (Exception ignored) {}
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @SuppressWarnings("unchecked")
    private void setupPerson(FxRobot robot) {
        robot.interact(() -> {
            Person p = personService.createPerson("Test", "Person", "111");
            p.getIncomeSources().add(new EmploymentIncome("I1", 15000, "Зарплата", "Google", true));
            p.getIncomeSources().add(new GiftIncome("G1", 5000, "Подарунок", "Друг", "друг"));
            p.getTaxBenefits().add(new ChildBenefit("B1", 500, "Діти", 2));
            repository.update(p);
            var table = (TableView<Person>) robot.lookup(".data-table").query();
            table.getItems().setAll(personService.findAll());
            table.getSelectionModel().selectFirst();
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    private void selectTab(FxRobot robot, int index) {
        robot.interact(() -> {
            TabPane tabs = robot.lookup(".main-tabs").query();
            tabs.getSelectionModel().select(index);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    // ── Базові ───────────────────────────────────────────────────────────
    @Test void title() { assertTrue(stage.getTitle().contains("tester")); }
    @Test void tabs(FxRobot r) { assertEquals(4, ((TabPane) r.lookup(".main-tabs").query()).getTabs().size()); }
    @Test void statusBar(FxRobot r) { assertNotNull(r.lookup(".status-bar").query()); }
    @Test void sidebar(FxRobot r) { assertNotNull(r.lookup(".sidebar-panel").tryQuery().orElse(null)); }
    @Test void personTable(FxRobot r) { assertNotNull(r.lookup(".data-table").query()); }

    // ── Особи ────────────────────────────────────────────────────────────
    @Test void addPersonDialog(FxRobot r) { r.clickOn("Додати особу"); closeDialogs(r); }
    @Test void editNoSelection(FxRobot r) { r.clickOn("Редагувати"); closeDialogs(r); }
    @Test void deleteNoSelection(FxRobot r) { r.clickOn("Видалити"); closeDialogs(r); }
    @Test void editWithSelection(FxRobot r) { setupPerson(r); r.clickOn("Редагувати"); closeDialogs(r); }
    @Test void deleteWithSelection(FxRobot r) { setupPerson(r); r.clickOn("Видалити"); closeDialogs(r); }
    @Test void selectPerson(FxRobot r) { setupPerson(r); assertNotNull(personService.getCurrentPerson()); }

    // ── Доходи (вкладка 0) ───────────────────────────────────────────────
    @Test void addSalaryNoPersonAlert(FxRobot r) { r.clickOn("Додати зарплату"); closeDialogs(r); }
    @Test void addSalaryDialog(FxRobot r) { setupPerson(r); r.clickOn("Додати зарплату"); closeDialogs(r); }
    @Test void addGiftDialog(FxRobot r) { setupPerson(r); r.clickOn("Додати подарунок"); closeDialogs(r); }
    @Test void addMaterialAidDialog(FxRobot r) { setupPerson(r); r.clickOn("Додати мат. допомогу"); closeDialogs(r); }
    @Test void addRoyaltyDialog(FxRobot r) { setupPerson(r); r.clickOn("Авторська винагорода"); closeDialogs(r); }
    @Test void addPropertySaleDialog(FxRobot r) { setupPerson(r); r.clickOn("Продаж майна"); closeDialogs(r); }
    @Test void addTransferDialog(FxRobot r) { setupPerson(r); r.clickOn("Переказ з-за кордону"); closeDialogs(r); }
    @Test void deleteIncomeNoSelection(FxRobot r) { setupPerson(r); r.clickOn("Видалити дохід"); closeDialogs(r); }

    // ── Пільги (вкладка 1) ───────────────────────────────────────────────
    @Test void childBenefitDialog(FxRobot r) { setupPerson(r); selectTab(r, 1); r.clickOn("Пільга на дітей"); closeDialogs(r); }
    @Test void materialAidBenefitDialog(FxRobot r) { setupPerson(r); selectTab(r, 1); r.clickOn("Пільга на мат. допомогу"); closeDialogs(r); }
    @Test void deleteBenefitNoSelection(FxRobot r) { setupPerson(r); selectTab(r, 1); r.clickOn("Видалити пільгу"); closeDialogs(r); }

    // ── Розрахунок (вкладка 2) ───────────────────────────────────────────
    @Test void calcNoPersonAlert(FxRobot r) { selectTab(r, 2); r.clickOn("Розрахувати податки"); closeDialogs(r); }
    @Test void calcWithPerson(FxRobot r) { setupPerson(r); selectTab(r, 2); r.clickOn("Розрахувати податки"); WaitForAsyncUtils.waitForFxEvents(); }
    @Test void sortAscNoPersonAlert(FxRobot r) { selectTab(r, 2); r.clickOn("Сортувати (зростання)"); closeDialogs(r); }
    @Test void sortAscWithPerson(FxRobot r) { setupPerson(r); selectTab(r, 2); r.clickOn("Сортувати (зростання)"); WaitForAsyncUtils.waitForFxEvents(); }
    @Test void sortDescWithPerson(FxRobot r) { setupPerson(r); selectTab(r, 2); r.clickOn("Сортувати (спадання)"); WaitForAsyncUtils.waitForFxEvents(); }
    @Test void findNoPersonAlert(FxRobot r) { selectTab(r, 2); r.clickOn("Знайти"); closeDialogs(r); }
    @Test void findWithPerson(FxRobot r) { setupPerson(r); selectTab(r, 2); r.clickOn("Знайти"); WaitForAsyncUtils.waitForFxEvents(); }

    // ── Звіт (вкладка 3) ─────────────────────────────────────────────────
    @Test void reportNoPersonAlert(FxRobot r) { selectTab(r, 3); r.clickOn("Згенерувати звіт"); closeDialogs(r); }
    @Test void reportDetailed(FxRobot r) { setupPerson(r); selectTab(r, 3); r.clickOn("Згенерувати звіт"); WaitForAsyncUtils.waitForFxEvents(); }
    @Test void reportSummary(FxRobot r) { setupPerson(r); selectTab(r, 3); r.clickOn("Короткий"); WaitForAsyncUtils.waitForFxEvents(); r.clickOn("Згенерувати звіт"); WaitForAsyncUtils.waitForFxEvents(); }
    @Test void reportSwitchDetailed(FxRobot r) { selectTab(r, 3); r.clickOn("Короткий"); WaitForAsyncUtils.waitForFxEvents(); r.clickOn("Детальний"); WaitForAsyncUtils.waitForFxEvents(); }
    @Test void exportNoFilenameAlert(FxRobot r) { setupPerson(r); selectTab(r, 3); r.clickOn("Експортувати у файл"); closeDialogs(r); }

    @Test void exportNoPersonAlert(FxRobot r) {
        selectTab(r, 3);
        r.interact(() -> r.lookup(".text-field").queryAll().stream()
                .filter(n -> n instanceof TextField).map(n -> (TextField) n)
                .filter(f -> f.getPromptText() != null && f.getPromptText().contains("файл"))
                .findFirst().ifPresent(f -> f.setText("nopers")));
        r.clickOn("Експортувати у файл"); closeDialogs(r);
    }

    @Test void exportSuccess(FxRobot r) {
        setupPerson(r); selectTab(r, 3);
        r.interact(() -> r.lookup(".text-field").queryAll().stream()
                .filter(n -> n instanceof TextField).map(n -> (TextField) n)
                .filter(f -> f.getPromptText() != null && f.getPromptText().contains("файл"))
                .findFirst().ifPresent(f -> f.setText("mc_test")));
        r.clickOn("Експортувати у файл"); WaitForAsyncUtils.waitForFxEvents();
        new File("data/mc_test.txt").delete();
    }
}
