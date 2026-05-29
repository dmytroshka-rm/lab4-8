package taxsystem.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import taxsystem.domain.*;
import taxsystem.repository.DataRepository;
import taxsystem.service.PersonService;
import taxsystem.service.TaxCalculatorService;
import taxsystem.service.TaxReportGenerator;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MainController {

    private static final Logger log = LogManager.getLogger(MainController.class);

    private final Stage stage;
    private final PersonService personService;
    private final TaxCalculatorService taxService;
    private final TaxReportGenerator reportGenerator;
    private final DataRepository repository;

    private TableView<Person> personTable;
    private ObservableList<Person> personList;

    private TableView<IncomeSource> incomeTable;
    private ObservableList<IncomeSource> incomeList;

    private TableView<TaxBenefit> benefitTable;
    private ObservableList<TaxBenefit> benefitList;

    private TextArea reportArea;
    private Label statusLabel;
    private Label userLabel;
    private User currentUser;

    public MainController(Stage stage, PersonService personService,
                          TaxCalculatorService taxService,
                          TaxReportGenerator reportGenerator,
                          DataRepository repository) {
        this.stage = stage;
        this.personService = personService;
        this.taxService = taxService;
        this.reportGenerator = reportGenerator;
        this.repository = repository;

        this.personList = FXCollections.observableArrayList();
        this.incomeList = FXCollections.observableArrayList();
        this.benefitList = FXCollections.observableArrayList();
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-root");
        root.setPadding(new Insets(14));

        root.setLeft(createPersonPanel());
        root.setCenter(createMainTabs());
        root.setBottom(createStatusBar());

        refreshPersonList();

        Scene scene = new Scene(root, 1100, 700);
        UiStyles.applyAppTheme(scene);
        String titleSuffix = (currentUser != null) ? " [" + currentUser.getUsername() + "]" : "";
        stage.setTitle("Податкова система — Розрахунок податків фізичних осіб" + titleSuffix);
        stage.setMinWidth(900);
        stage.setMinHeight(560);
        stage.setScene(scene);
        stage.show();
    }

    // === Панель осіб (ліва частина) ===

    @SuppressWarnings("unchecked")
    private VBox createPersonPanel() {
        VBox panel = new VBox(10);
        panel.getStyleClass().add("sidebar-panel");
        panel.setPrefWidth(290);
        panel.setMinWidth(250);
        BorderPane.setMargin(panel, new Insets(0, 14, 0, 0));

        Label title = new Label("Платники податків");
        title.getStyleClass().add("section-title");

        personTable = new TableView<>(personList);
        personTable.getStyleClass().add("data-table");
        VBox.setVgrow(personTable, Priority.ALWAYS);
        personTable.setMinHeight(200);

        TableColumn<Person, String> nameCol = new TableColumn<>("Ім'я");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        nameCol.setPrefWidth(120);

        TableColumn<Person, String> surnameCol = new TableColumn<>("Прізвище");
        surnameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        surnameCol.setPrefWidth(130);

        personTable.getColumns().addAll(nameCol, surnameCol);
        personTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> onPersonSelected(newVal)
        );

        Button addBtn = new Button("Додати особу");
        addBtn.setMaxWidth(Double.MAX_VALUE);
        UiStyles.styleButton(addBtn, UiStyles.ButtonVariant.SUCCESS);
        addBtn.setOnAction(e -> showAddPersonDialog());

        Button editBtn = new Button("Редагувати");
        editBtn.setMaxWidth(Double.MAX_VALUE);
        UiStyles.styleButton(editBtn, UiStyles.ButtonVariant.SECONDARY);
        editBtn.setOnAction(e -> showEditPersonDialog());

        Button deleteBtn = new Button("Видалити");
        deleteBtn.setMaxWidth(Double.MAX_VALUE);
        UiStyles.styleButton(deleteBtn, UiStyles.ButtonVariant.DANGER);
        deleteBtn.setOnAction(e -> deleteSelectedPerson());

        panel.getChildren().addAll(title, personTable, addBtn, editBtn, deleteBtn);
        return panel;
    }

    // === Вкладки (центральна частина) ===

    private TabPane createMainTabs() {
        TabPane tabs = new TabPane();
        tabs.getStyleClass().add("main-tabs");
        tabs.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        Tab incomeTab = new Tab("Доходи", createIncomeTab());
        incomeTab.setClosable(false);

        Tab benefitTab = new Tab("Пільги", createBenefitTab());
        benefitTab.setClosable(false);

        Tab taxTab = new Tab("Розрахунок", createTaxTab());
        taxTab.setClosable(false);

        Tab reportTab = new Tab("Звіт", createReportTab());
        reportTab.setClosable(false);

        tabs.getTabs().addAll(incomeTab, benefitTab, taxTab, reportTab);
        return tabs;
    }

    // === Вкладка "Доходи" ===

    @SuppressWarnings("unchecked")
    private VBox createIncomeTab() {
        VBox box = new VBox(12);
        box.getStyleClass().add("tab-content");
        box.setPadding(new Insets(12));

        incomeTable = new TableView<>(incomeList);
        incomeTable.getStyleClass().add("data-table");

        TableColumn<IncomeSource, String> typeCol = new TableColumn<>("Тип");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("incomeType"));
        typeCol.setPrefWidth(150);

        TableColumn<IncomeSource, String> descCol = new TableColumn<>("Опис");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descCol.setPrefWidth(200);

        TableColumn<IncomeSource, Double> amountCol = new TableColumn<>("Сума (грн)");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountCol.setPrefWidth(120);

        TableColumn<IncomeSource, Double> taxCol = new TableColumn<>("Податок (грн)");
        taxCol.setCellValueFactory(new PropertyValueFactory<>("taxAmount"));
        taxCol.setPrefWidth(120);

        incomeTable.getColumns().addAll(typeCol, descCol, amountCol, taxCol);

        HBox buttons = new HBox(10);
        buttons.getStyleClass().add("action-toolbar");
        Button addEmpBtn = new Button("Додати зарплату");
        UiStyles.styleButton(addEmpBtn, UiStyles.ButtonVariant.OUTLINE);
        addEmpBtn.setOnAction(e -> showAddEmploymentDialog());

        Button addGiftBtn = new Button("Додати подарунок");
        UiStyles.styleButton(addGiftBtn, UiStyles.ButtonVariant.OUTLINE);
        addGiftBtn.setOnAction(e -> showAddGiftDialog());

        Button addAidBtn = new Button("Додати мат. допомогу");
        UiStyles.styleButton(addAidBtn, UiStyles.ButtonVariant.OUTLINE);
        addAidBtn.setOnAction(e -> showAddMaterialAidDialog());

        Button addRoyaltyBtn = new Button("Авторська винагорода");
        UiStyles.styleButton(addRoyaltyBtn, UiStyles.ButtonVariant.OUTLINE);
        addRoyaltyBtn.setOnAction(e -> showAddRoyaltyDialog());

        Button addSaleBtn = new Button("Продаж майна");
        UiStyles.styleButton(addSaleBtn, UiStyles.ButtonVariant.OUTLINE);
        addSaleBtn.setOnAction(e -> showAddPropertySaleDialog());

        Button addTransferBtn = new Button("Переказ з-за кордону");
        UiStyles.styleButton(addTransferBtn, UiStyles.ButtonVariant.OUTLINE);
        addTransferBtn.setOnAction(e -> showAddForeignTransferDialog());

        Button deleteIncBtn = new Button("Видалити дохід");
        UiStyles.styleButton(deleteIncBtn, UiStyles.ButtonVariant.DANGER);
        deleteIncBtn.setOnAction(e -> deleteSelectedIncome());

        HBox buttons2 = new HBox(10);
        buttons2.getStyleClass().add("action-toolbar");
        buttons2.getChildren().addAll(addRoyaltyBtn, addSaleBtn, addTransferBtn);
        buttons.getChildren().addAll(addEmpBtn, addGiftBtn, addAidBtn, deleteIncBtn);

        VBox toolbars = new VBox(8);
        toolbars.getStyleClass().add("action-toolbar-wrap");
        toolbars.getChildren().addAll(buttons, buttons2);

        box.getChildren().addAll(incomeTable, toolbars);
        VBox.setVgrow(incomeTable, Priority.ALWAYS);
        return box;
    }

    // === Вкладка "Пільги" ===

    @SuppressWarnings("unchecked")
    private VBox createBenefitTab() {
        VBox box = new VBox(12);
        box.getStyleClass().add("tab-content");
        box.setPadding(new Insets(12));

        benefitTable = new TableView<>(benefitList);
        benefitTable.getStyleClass().add("data-table");

        TableColumn<TaxBenefit, String> descCol = new TableColumn<>("Опис");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descCol.setPrefWidth(250);

        TableColumn<TaxBenefit, Double> amountCol = new TableColumn<>("Сума (грн)");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountCol.setPrefWidth(120);

        TableColumn<TaxBenefit, Boolean> activeCol = new TableColumn<>("Активна");
        activeCol.setCellValueFactory(new PropertyValueFactory<>("active"));
        activeCol.setPrefWidth(80);

        benefitTable.getColumns().addAll(descCol, amountCol, activeCol);

        HBox buttons = new HBox(10);
        buttons.getStyleClass().add("action-toolbar");
        Button addBtn = new Button("Пільга на дітей");
        UiStyles.styleButton(addBtn, UiStyles.ButtonVariant.OUTLINE);
        addBtn.setOnAction(e -> showAddChildBenefitDialog());

        Button addMabBtn = new Button("Пільга на мат. допомогу");
        UiStyles.styleButton(addMabBtn, UiStyles.ButtonVariant.OUTLINE);
        addMabBtn.setOnAction(e -> showAddMaterialAidBenefitDialog());

        Button deleteBtn = new Button("Видалити пільгу");
        UiStyles.styleButton(deleteBtn, UiStyles.ButtonVariant.DANGER);
        deleteBtn.setOnAction(e -> deleteSelectedBenefit());

        buttons.getChildren().addAll(addBtn, addMabBtn, deleteBtn);

        box.getChildren().addAll(benefitTable, buttons);
        VBox.setVgrow(benefitTable, Priority.ALWAYS);
        return box;
    }

    // === Вкладка "Розрахунок" ===

    private VBox createTaxTab() {
        VBox box = new VBox(14);
        box.getStyleClass().add("tab-content");
        box.setPadding(new Insets(12));

        HBox calcButtons = new HBox(10);
        calcButtons.getStyleClass().add("action-toolbar");

        Button calcBtn = new Button("Розрахувати податки");
        UiStyles.styleButton(calcBtn, UiStyles.ButtonVariant.PRIMARY);
        calcBtn.setOnAction(e -> calculateTaxes());

        Button sortAscBtn = new Button("Сортувати (зростання)");
        UiStyles.styleButton(sortAscBtn, UiStyles.ButtonVariant.OUTLINE);
        sortAscBtn.setOnAction(e -> sortTaxes(true));

        Button sortDescBtn = new Button("Сортувати (спадання)");
        UiStyles.styleButton(sortDescBtn, UiStyles.ButtonVariant.OUTLINE);
        sortDescBtn.setOnAction(e -> sortTaxes(false));

        calcButtons.getChildren().addAll(calcBtn, sortAscBtn, sortDescBtn);

        HBox searchBox = new HBox(10);
        searchBox.getStyleClass().add("control-strip");
        searchBox.setAlignment(Pos.CENTER_LEFT);
        Label minLabel = new Label("Мін. податок:");
        TextField minField = new TextField();
        minField.setPrefWidth(100);
        minField.setPromptText("0");
        Label maxLabel = new Label("Макс. податок:");
        TextField maxField = new TextField();
        maxField.setPrefWidth(100);
        maxField.setPromptText("99999");
        Button searchBtn = new Button("Знайти");
        UiStyles.styleButton(searchBtn, UiStyles.ButtonVariant.SECONDARY);
        searchBtn.setOnAction(e -> findByTaxRange(minField.getText(), maxField.getText()));

        searchBox.getChildren().addAll(minLabel, minField, maxLabel, maxField, searchBtn);

        Label resultTitle = new Label("Результати:");
        resultTitle.getStyleClass().add("section-title");

        reportArea = new TextArea();
        reportArea.setEditable(false);
        reportArea.setPrefHeight(350);

        box.getChildren().addAll(calcButtons, searchBox, resultTitle, reportArea);
        VBox.setVgrow(reportArea, Priority.ALWAYS);
        return box;
    }

    // === Вкладка "Звіт" ===

    private VBox createReportTab() {
        VBox box = new VBox(12);
        box.getStyleClass().add("tab-content");
        box.setPadding(new Insets(12));

        HBox modeBox = new HBox(10);
        modeBox.getStyleClass().add("control-strip");
        modeBox.setAlignment(Pos.CENTER_LEFT);

        ToggleGroup modeGroup = new ToggleGroup();
        RadioButton detailedBtn = new RadioButton("Детальний");
        detailedBtn.setToggleGroup(modeGroup);
        detailedBtn.setSelected(true);
        detailedBtn.setOnAction(e -> reportGenerator.setDetailedMode());

        RadioButton summaryBtn = new RadioButton("Короткий");
        summaryBtn.setToggleGroup(modeGroup);
        summaryBtn.setOnAction(e -> reportGenerator.setSummaryMode());

        Button generateBtn = new Button("Згенерувати звіт");
        UiStyles.styleButton(generateBtn, UiStyles.ButtonVariant.PRIMARY);

        Label modeLbl = new Label("Режим:");
        modeLbl.getStyleClass().add("form-label");
        modeBox.getChildren().addAll(modeLbl, detailedBtn, summaryBtn, generateBtn);

        TextArea reportOutput = new TextArea();
        reportOutput.setEditable(false);
        reportOutput.setId("reportOutput");

        HBox exportBox = new HBox(10);
        exportBox.getStyleClass().add("control-strip");
        exportBox.setAlignment(Pos.CENTER_LEFT);
        TextField filenameField = new TextField();
        filenameField.setPromptText("Назва файлу");
        filenameField.setPrefWidth(220);

        Button exportBtn = new Button("Експортувати у файл");
        UiStyles.styleButton(exportBtn, UiStyles.ButtonVariant.SUCCESS);
        exportBtn.setOnAction(e -> {
            String filename = filenameField.getText().trim();
            if (filename.isEmpty()) {
                showAlert("Помилка", "Введіть назву файлу.");
                return;
            }
            Person p = personService.getCurrentPerson();
            if (p == null) {
                showAlert("Помилка", "Оберіть особу.");
                return;
            }
            taxService.recalcTaxes(p);
            String report = reportGenerator.generateReport(p);
            repository.exportTaxReport(report, filename);
            setStatus("Звіт експортовано у файл: " + filename + ".txt");
            log.info("Звіт експортовано: {}", filename);
        });

        exportBox.getChildren().addAll(filenameField, exportBtn);

        generateBtn.setOnAction(e -> {
            Person p = personService.getCurrentPerson();
            if (p == null) {
                showAlert("Помилка", "Оберіть особу.");
                return;
            }
            taxService.recalcTaxes(p);
            String report = reportGenerator.generateReport(p);
            reportOutput.setText(report);
            setStatus("Звіт згенеровано.");
        });

        box.getChildren().addAll(modeBox, reportOutput, exportBox);
        VBox.setVgrow(reportOutput, Priority.ALWAYS);
        return box;
    }

    // === Статус бар ===

    private HBox createStatusBar() {
        HBox bar = new HBox();
        bar.getStyleClass().add("status-bar");

        statusLabel = new Label("Готово");
        statusLabel.setStyle("-fx-text-fill: #64748b;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        userLabel = new Label();
        userLabel.getStyleClass().add("status-user");
        if (currentUser != null) {
            userLabel.setText("Користувач: " + currentUser.getUsername());
        }

        bar.getChildren().addAll(statusLabel, spacer, userLabel);
        return bar;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    // === Обробники подій ===

    private void onPersonSelected(Person person) {
        if (person == null) return;
        personService.setCurrentPerson(person);
        refreshIncomeList();
        refreshBenefitList();
        setStatus("Обрано: " + person.getFirstName() + " " + person.getLastName());
    }

    private void refreshPersonList() {
        personList.setAll(personService.findAll());
    }

    private void refreshIncomeList() {
        Person p = personService.getCurrentPerson();
        if (p != null) {
            taxService.recalcTaxes(p);
            incomeList.setAll(p.getIncomeSources());
        } else {
            incomeList.clear();
        }
    }

    private void refreshBenefitList() {
        Person p = personService.getCurrentPerson();
        if (p != null) {
            benefitList.setAll(p.getTaxBenefits());
        } else {
            benefitList.clear();
        }
    }

    // === Діалоги CRUD для осіб ===

    private void showAddPersonDialog() {
        Dialog<Person> dialog = new Dialog<>();
        dialog.setTitle("Нова особа");
        dialog.setHeaderText("Введіть дані платника податків");

        ButtonType saveType = new ButtonType("Зберегти", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        GridPane grid = createFormGrid();
        TextField firstNameField = new TextField();
        TextField lastNameField = new TextField();
        TextField taxIdField = new TextField();
        taxIdField.setPromptText("1234567890");

        grid.add(new Label("Ім'я:"), 0, 0);
        grid.add(firstNameField, 1, 0);
        grid.add(new Label("Прізвище:"), 0, 1);
        grid.add(lastNameField, 1, 1);
        grid.add(new Label("ІПН:"), 0, 2);
        grid.add(taxIdField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveType) {
                String fn = firstNameField.getText().trim();
                String ln = lastNameField.getText().trim();
                String tid = taxIdField.getText().trim();
                if (fn.isEmpty() || ln.isEmpty() || tid.isEmpty()) return null;
                return personService.createPerson(fn, ln, tid);
            }
            return null;
        });

        Optional<Person> result = dialog.showAndWait();
        result.ifPresent(p -> {
            refreshPersonList();
            personTable.getSelectionModel().selectLast();
            setStatus("Особу додано: " + p.getFirstName() + " " + p.getLastName());
            log.info("Додано нову особу через GUI: {} {}", p.getFirstName(), p.getLastName());
        });
    }

    private void showEditPersonDialog() {
        Person selected = personTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Помилка", "Оберіть особу для редагування.");
            return;
        }

        Dialog<Person> dialog = new Dialog<>();
        dialog.setTitle("Редагування особи");

        ButtonType saveType = new ButtonType("Зберегти", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        GridPane grid = createFormGrid();
        TextField firstNameField = new TextField(selected.getFirstName());
        TextField lastNameField = new TextField(selected.getLastName());
        TextField taxIdField = new TextField(selected.getTaxId());

        grid.add(new Label("Ім'я:"), 0, 0);
        grid.add(firstNameField, 1, 0);
        grid.add(new Label("Прізвище:"), 0, 1);
        grid.add(lastNameField, 1, 1);
        grid.add(new Label("ІПН:"), 0, 2);
        grid.add(taxIdField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveType) {
                selected.setFirstName(firstNameField.getText().trim());
                selected.setLastName(lastNameField.getText().trim());
                selected.setTaxId(taxIdField.getText().trim());
                repository.update(selected);
                return selected;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(p -> {
            refreshPersonList();
            setStatus("Особу оновлено: " + p.getFirstName() + " " + p.getLastName());
        });
    }

    private void deleteSelectedPerson() {
        Person selected = personTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Помилка", "Оберіть особу для видалення.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Видалити особу " + selected.getFirstName() + " " + selected.getLastName() + "?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Підтвердження");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                personService.deletePerson(selected.getPersonId());
                refreshPersonList();
                incomeList.clear();
                benefitList.clear();
                setStatus("Особу видалено.");
                log.info("Видалено особу через GUI: {}", selected.getPersonId());
            }
        });
    }

    // === Діалоги додавання доходів ===

    private void showAddEmploymentDialog() {
        if (!checkPersonSelected()) return;

        Dialog<IncomeSource> dialog = new Dialog<>();
        dialog.setTitle("Дохід від роботи");

        ButtonType saveType = new ButtonType("Додати", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        GridPane grid = createFormGrid();
        TextField amountField = new TextField();
        amountField.setPromptText("15000");
        TextField employerField = new TextField();
        TextField descField = new TextField();
        CheckBox mainJobCb = new CheckBox("Основне місце роботи");
        mainJobCb.setSelected(true);

        grid.add(new Label("Сума (грн):"), 0, 0); grid.add(amountField, 1, 0);
        grid.add(new Label("Роботодавець:"), 0, 1); grid.add(employerField, 1, 1);
        grid.add(new Label("Опис:"), 0, 2);      grid.add(descField, 1, 2);
        grid.add(mainJobCb, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveType) {
                try {
                    double amount = Double.parseDouble(amountField.getText().trim());
                    return new EmploymentIncome(UUID.randomUUID().toString().substring(0, 8), amount,
                            descField.getText().trim(), employerField.getText().trim(), mainJobCb.isSelected());
                } catch (NumberFormatException e) {
                    showAlert("Помилка", "Невірний формат суми.");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(income -> {
            personService.addIncome(income);
            refreshIncomeList();
            setStatus("Додано дохід від роботи: " + income.getAmount() + " грн");
        });
    }

    private void showAddGiftDialog() {
        if (!checkPersonSelected()) return;

        Dialog<IncomeSource> dialog = new Dialog<>();
        dialog.setTitle("Подарунок");

        ButtonType saveType = new ButtonType("Додати", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        GridPane grid = createFormGrid();
        TextField amountField = new TextField();
        TextField donorField = new TextField();
        TextField relationField = new TextField();
        relationField.setPromptText("батько, друг, сестра...");
        TextField descField = new TextField();

        grid.add(new Label("Сума (грн):"), 0, 0);     grid.add(amountField, 1, 0);
        grid.add(new Label("Дарувальник:"), 0, 1);     grid.add(donorField, 1, 1);
        grid.add(new Label("Родинний зв'язок:"), 0, 2); grid.add(relationField, 1, 2);
        grid.add(new Label("Опис:"), 0, 3);            grid.add(descField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveType) {
                try {
                    double amount = Double.parseDouble(amountField.getText().trim());
                    return new GiftIncome(UUID.randomUUID().toString().substring(0, 8), amount,
                            descField.getText().trim(), donorField.getText().trim(), relationField.getText().trim());
                } catch (NumberFormatException e) {
                    showAlert("Помилка", "Невірний формат суми.");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(income -> {
            personService.addIncome(income);
            refreshIncomeList();
            setStatus("Додано подарунок: " + income.getAmount() + " грн");
        });
    }

    private void showAddMaterialAidDialog() {
        if (!checkPersonSelected()) return;

        Dialog<IncomeSource> dialog = new Dialog<>();
        dialog.setTitle("Матеріальна допомога");

        ButtonType saveType = new ButtonType("Додати", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        GridPane grid = createFormGrid();
        TextField amountField = new TextField();
        TextField aidTypeField = new TextField();
        aidTypeField.setPromptText("одноразова, соціальна...");
        TextField descField = new TextField();
        CheckBox taxableCb = new CheckBox("Оподатковується");
        taxableCb.setSelected(true);

        grid.add(new Label("Сума (грн):"), 0, 0);   grid.add(amountField, 1, 0);
        grid.add(new Label("Тип допомоги:"), 0, 1); grid.add(aidTypeField, 1, 1);
        grid.add(new Label("Опис:"), 0, 2);         grid.add(descField, 1, 2);
        grid.add(taxableCb, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveType) {
                try {
                    double amount = Double.parseDouble(amountField.getText().trim());
                    return new MaterialAid(UUID.randomUUID().toString().substring(0, 8), amount,
                            descField.getText().trim(), aidTypeField.getText().trim(), taxableCb.isSelected());
                } catch (NumberFormatException e) {
                    showAlert("Помилка", "Невірний формат суми.");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(income -> {
            personService.addIncome(income);
            refreshIncomeList();
            setStatus("Додано матеріальну допомогу: " + income.getAmount() + " грн");
        });
    }

    private void showAddRoyaltyDialog() {
        if (!checkPersonSelected()) return;

        Dialog<IncomeSource> dialog = new Dialog<>();
        dialog.setTitle("Авторська винагорода");

        ButtonType saveType = new ButtonType("Додати", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        GridPane grid = createFormGrid();
        TextField amountField = new TextField();
        amountField.setPromptText("5000");
        TextField workTitleField = new TextField();
        workTitleField.setPromptText("Назва твору");
        TextField workTypeField = new TextField();
        workTypeField.setPromptText("книга, стаття, програма...");
        TextField descField = new TextField();

        grid.add(new Label("Сума (грн):"), 0, 0);    grid.add(amountField, 1, 0);
        grid.add(new Label("Назва твору:"), 0, 1);    grid.add(workTitleField, 1, 1);
        grid.add(new Label("Тип твору:"), 0, 2);      grid.add(workTypeField, 1, 2);
        grid.add(new Label("Опис:"), 0, 3);           grid.add(descField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveType) {
                try {
                    double amount = Double.parseDouble(amountField.getText().trim());
                    return new RoyaltyIncome(UUID.randomUUID().toString().substring(0, 8), amount,
                            descField.getText().trim(), workTitleField.getText().trim(),
                            workTypeField.getText().trim());
                } catch (NumberFormatException e) {
                    showAlert("Помилка", "Невірний формат суми.");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(income -> {
            personService.addIncome(income);
            refreshIncomeList();
            setStatus("Додано авторську винагороду: " + income.getAmount() + " грн");
        });
    }

    private void showAddPropertySaleDialog() {
        if (!checkPersonSelected()) return;

        Dialog<IncomeSource> dialog = new Dialog<>();
        dialog.setTitle("Продаж майна");

        ButtonType saveType = new ButtonType("Додати", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        GridPane grid = createFormGrid();
        TextField amountField = new TextField();
        amountField.setPromptText("100000");
        TextField propTypeField = new TextField();
        propTypeField.setPromptText("квартира, авто, земля...");
        TextField descField = new TextField();
        CheckBox firstSaleCb = new CheckBox("Перший продаж за рік (ставка 5%)");
        firstSaleCb.setSelected(true);

        grid.add(new Label("Сума (грн):"), 0, 0);    grid.add(amountField, 1, 0);
        grid.add(new Label("Тип майна:"), 0, 1);     grid.add(propTypeField, 1, 1);
        grid.add(new Label("Опис:"), 0, 2);           grid.add(descField, 1, 2);
        grid.add(firstSaleCb, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveType) {
                try {
                    double amount = Double.parseDouble(amountField.getText().trim());
                    return new PropertySaleIncome(UUID.randomUUID().toString().substring(0, 8), amount,
                            descField.getText().trim(), propTypeField.getText().trim(),
                            firstSaleCb.isSelected());
                } catch (NumberFormatException e) {
                    showAlert("Помилка", "Невірний формат суми.");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(income -> {
            personService.addIncome(income);
            refreshIncomeList();
            setStatus("Додано продаж майна: " + income.getAmount() + " грн");
        });
    }

    private void showAddForeignTransferDialog() {
        if (!checkPersonSelected()) return;

        Dialog<IncomeSource> dialog = new Dialog<>();
        dialog.setTitle("Переказ з-за кордону");

        ButtonType saveType = new ButtonType("Додати", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        GridPane grid = createFormGrid();
        TextField amountField = new TextField();
        amountField.setPromptText("10000 (в грн)");
        TextField countryField = new TextField();
        countryField.setPromptText("Німеччина, США...");
        TextField currencyField = new TextField();
        currencyField.setPromptText("EUR, USD...");
        TextField descField = new TextField();

        grid.add(new Label("Сума (грн):"), 0, 0);  grid.add(amountField, 1, 0);
        grid.add(new Label("Країна:"), 0, 1);       grid.add(countryField, 1, 1);
        grid.add(new Label("Валюта:"), 0, 2);       grid.add(currencyField, 1, 2);
        grid.add(new Label("Опис:"), 0, 3);         grid.add(descField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveType) {
                try {
                    double amount = Double.parseDouble(amountField.getText().trim());
                    return new ForeignTransferIncome(UUID.randomUUID().toString().substring(0, 8), amount,
                            descField.getText().trim(), countryField.getText().trim(),
                            currencyField.getText().trim());
                } catch (NumberFormatException e) {
                    showAlert("Помилка", "Невірний формат суми.");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(income -> {
            personService.addIncome(income);
            refreshIncomeList();
            setStatus("Додано переказ з-за кордону: " + income.getAmount() + " грн");
        });
    }

    private void deleteSelectedIncome() {
        IncomeSource selected = incomeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Помилка", "Оберіть дохід для видалення.");
            return;
        }
        personService.removeIncome(selected.getSourceId());
        refreshIncomeList();
        setStatus("Дохід видалено.");
    }

    // === Діалог додавання пільги ===

    private void showAddChildBenefitDialog() {
        if (!checkPersonSelected()) return;

        Dialog<TaxBenefit> dialog = new Dialog<>();
        dialog.setTitle("Пільга на дітей");

        ButtonType saveType = new ButtonType("Додати", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        GridPane grid = createFormGrid();
        TextField amountField = new TextField();
        amountField.setPromptText("1000");
        TextField childCountField = new TextField();
        childCountField.setPromptText("2");
        TextField descField = new TextField();

        grid.add(new Label("Сума на дитину (грн):"), 0, 0); grid.add(amountField, 1, 0);
        grid.add(new Label("Кількість дітей:"), 0, 1);      grid.add(childCountField, 1, 1);
        grid.add(new Label("Опис:"), 0, 2);                 grid.add(descField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveType) {
                try {
                    double amount = Double.parseDouble(amountField.getText().trim());
                    int count = Integer.parseInt(childCountField.getText().trim());
                    return new ChildBenefit(UUID.randomUUID().toString().substring(0, 8), amount, descField.getText().trim(), count);
                } catch (NumberFormatException e) {
                    showAlert("Помилка", "Невірний формат даних.");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(benefit -> {
            personService.addBenefit(benefit);
            refreshBenefitList();
            setStatus("Додано пільгу: " + benefit.getDescription());
        });
    }

    private void showAddMaterialAidBenefitDialog() {
        if (!checkPersonSelected()) return;

        Dialog<TaxBenefit> dialog = new Dialog<>();
        dialog.setTitle("Пільга на матеріальну допомогу");

        ButtonType saveType = new ButtonType("Додати", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        GridPane grid = createFormGrid();
        TextField amountField = new TextField();
        amountField.setPromptText("2000");
        TextField maxNonTaxField = new TextField();
        maxNonTaxField.setPromptText("4470");
        TextField descField = new TextField();

        grid.add(new Label("Сума пільги (грн):"), 0, 0);         grid.add(amountField, 1, 0);
        grid.add(new Label("Макс. неоподатковувана (грн):"), 0, 1); grid.add(maxNonTaxField, 1, 1);
        grid.add(new Label("Опис:"), 0, 2);                      grid.add(descField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveType) {
                try {
                    double amount = Double.parseDouble(amountField.getText().trim());
                    double maxNonTax = Double.parseDouble(maxNonTaxField.getText().trim());
                    return new MaterialAidBenefit(UUID.randomUUID().toString().substring(0, 8), amount,
                            descField.getText().trim(), maxNonTax);
                } catch (NumberFormatException e) {
                    showAlert("Помилка", "Невірний формат даних.");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(benefit -> {
            personService.addBenefit(benefit);
            refreshBenefitList();
            setStatus("Додано пільгу на мат. допомогу: " + benefit.getDescription());
        });
    }

    private void deleteSelectedBenefit() {
        TaxBenefit selected = benefitTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Помилка", "Оберіть пільгу для видалення.");
            return;
        }
        personService.removeBenefit(selected.getBenefitId());
        refreshBenefitList();
        setStatus("Пільгу видалено.");
    }

    // === Розрахунки ===

    private void calculateTaxes() {
        Person p = personService.getCurrentPerson();
        if (p == null) {
            showAlert("Помилка", "Оберіть особу.");
            return;
        }

        if (!taxService.validateTaxCalculation(p)) {
            showAlert("Помилка", "Дані некоректні. Перевірте суми доходів та пільг.");
            return;
        }

        taxService.recalcTaxes(p);
        double before = taxService.getTotalTaxBeforeBenefits(p);
        double after = taxService.getTotalTaxAfterBenefits(p);

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Платник: %s %s (ІПН: %s)%n%n", p.getFirstName(), p.getLastName(), p.getTaxId()));

        sb.append("--- Доходи ---\n");
        for (IncomeSource s : p.getIncomeSources()) {
            sb.append(String.format("  %-20s | %-25s | сума: %10.2f | податок: %10.2f%n",
                    s.getIncomeType(), s.getDescription(), s.getAmount(), s.getTaxAmount()));
        }

        sb.append(String.format("%n--- Підсумок ---%n"));
        sb.append(String.format("Сума податків до пільг:    %10.2f грн%n", before));
        sb.append(String.format("Сума податків після пільг: %10.2f грн%n", after));
        sb.append(String.format("Економія від пільг:        %10.2f грн%n", before - after));

        reportArea.setText(sb.toString());
        refreshIncomeList();
        setStatus(String.format("Податки розраховано. До пільг: %.2f, після: %.2f", before, after));
        log.info("Розраховано податки для {} {}: до пільг={}, після={}",
                p.getFirstName(), p.getLastName(), before, after);
    }

    private void sortTaxes(boolean ascending) {
        Person p = personService.getCurrentPerson();
        if (p == null) {
            showAlert("Помилка", "Оберіть особу.");
            return;
        }

        taxService.recalcTaxes(p);
        List<IncomeSource> sorted = taxService.sortByTax(p, ascending);
        incomeList.setAll(sorted);

        StringBuilder sb = new StringBuilder();
        sb.append("Сортування за податком (").append(ascending ? "зростання" : "спадання").append("):\n\n");
        for (IncomeSource s : sorted) {
            sb.append(String.format("  %-20s | %-25s | податок: %10.2f грн%n",
                    s.getIncomeType(), s.getDescription(), s.getTaxAmount()));
        }
        reportArea.setText(sb.toString());
        setStatus("Відсортовано за " + (ascending ? "зростанням" : "спаданням"));
    }

    private void findByTaxRange(String minText, String maxText) {
        Person p = personService.getCurrentPerson();
        if (p == null) {
            showAlert("Помилка", "Оберіть особу.");
            return;
        }

        double min, max;
        try {
            min = minText.isEmpty() ? 0 : Double.parseDouble(minText);
            max = maxText.isEmpty() ? Double.MAX_VALUE : Double.parseDouble(maxText);
        } catch (NumberFormatException e) {
            showAlert("Помилка", "Невірний формат чисел для діапазону.");
            return;
        }

        taxService.recalcTaxes(p);
        List<IncomeSource> found = taxService.findByTaxRange(p, min, max);

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Пошук у діапазоні від %.2f до %.2f грн:%n%n", min, max));

        if (found.isEmpty()) {
            sb.append("Не знайдено жодного доходу в цьому діапазоні.\n");
        } else {
            for (IncomeSource s : found) {
                sb.append(String.format("  %-20s | %-25s | податок: %10.2f грн%n",
                        s.getIncomeType(), s.getDescription(), s.getTaxAmount()));
            }
            sb.append(String.format("%nЗнайдено: %d%n", found.size()));
        }

        reportArea.setText(sb.toString());
        setStatus("Знайдено " + found.size() + " доходів у діапазоні");
    }

    // === Утиліти ===

    private boolean checkPersonSelected() {
        if (personService.getCurrentPerson() == null) {
            showAlert("Помилка", "Спочатку оберіть або створіть особу.");
            return false;
        }
        return true;
    }

    private GridPane createFormGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        return grid;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void setStatus(String text) {
        if (statusLabel != null) {
            statusLabel.setText(text);
        }
    }
}
