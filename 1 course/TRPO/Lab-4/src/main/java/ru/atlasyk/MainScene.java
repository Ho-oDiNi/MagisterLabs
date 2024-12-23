package ru.atlasyk;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import ru.atlasyk.interfaces.UserType;
import ru.atlasyk.list.LinkedList;

// Основной класс для JavaFX приложения
public class MainScene extends Application {

    private ListView<UserType> listView;
    private LinkedList dataList;
    private TextField inputField;
    private TextField indexField;
    private final UserType userType;

    public MainScene(UserType selectedType) {
        this.userType = selectedType;
    }


    @Override
    public void start(Stage primaryStage) {
        listView = new ListView<>();
        dataList = new LinkedList();

        inputField = new TextField();
        inputField.setPromptText("Введите элемент");

        indexField = new TextField();
        indexField.setPromptText("Индекс");

        Button addButton = new Button("Добавить элемент");
        Button removeButton = new Button("Удалить элемент по индексу");
        Button searchButton = new Button("Поиск по индексу");
        Button balanceButton = new Button("Сортировка");
        Button saveButton = new Button("Сохранить в файл");
        Button loadButton = new Button("Загрузить из файла");


        addButton.setOnAction(e -> addElement());
        removeButton.setOnAction(e -> removeElement());
        searchButton.setOnAction(e -> searchElement());
        saveButton.setOnAction(e -> saveToFile());
        loadButton.setOnAction(e -> loadFromFile());
        balanceButton.setOnAction(e -> sortList());

        // Размещение компонентов
        VBox buttonBox = new VBox(
                10,
                inputField,
                addButton,
                indexField,
                removeButton,
                searchButton,
                balanceButton,
                saveButton,
                loadButton
        );
        buttonBox.setPadding(new Insets(10));
        buttonBox.setPrefWidth(200);

        BorderPane root = new BorderPane();
        root.setCenter(listView);
        root.setRight(buttonBox);

        // Настройка сцены
        Scene scene = new Scene(root, 700, 400);
        primaryStage.setTitle("Лабораторная работа 4");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void addElement() {
        try {
            UserType value = userType.parseValue(inputField.getText());
            if (value != null) {
                dataList.add(value);
                refreshListView();
                inputField.clear();
            }
        } catch (Exception e) {
            showError(e.getLocalizedMessage());
        }

    }

    // Удалить элемент по индексу
    private void removeElement() {
        try {
            int index = Integer.parseInt(indexField.getText());
            if (index >= 0 && index < dataList.size()) {
                dataList.delete(index);
                refreshListView();
                indexField.clear();
            } else {
                showError("Некорректный индекс!");
            }
        } catch (NumberFormatException e) {
            showError("Введите числовой индекс!");
        }
    }

    // Найти элемент по индексу
    private void searchElement() {
        try {
            int index = Integer.parseInt(indexField.getText());
            indexField.clear();
            if (index >= 0 && index < dataList.size()) {
                UserType value = dataList.get(index);

                // Создаем метку с результатом
                var result = "Индекс: " + index + ", Значение: " + value;
                showInfo(result, "Результат поиска", "Результат поиска: ");

            } else {
                showError("Индекс вне диапазона!");
            }
        } catch (NumberFormatException e) {
            showError("Введите числовой индекс!");
        }
    }

    // "Балансировка" списка (пример: сортировка)
    private void sortList() {
        dataList.sort();
        refreshListView();
    }

    private void saveToFile() {
        try {
            int index = Integer.parseInt(indexField.getText());
            indexField.clear();
            if (index >= 0 && index < dataList.size()) {
                UserType value = dataList.get(index);
                value.writeToFileChar();
                showInfo("Успешно сохранено!", "Успешно!", "Успешное сохранение в файл");
            } else {
                showError("Индекс вне диапазона!");
            }
        } catch (NumberFormatException e) {
            showError("Введите числовой индекс!");
        } catch (IOException e) {
            showError("Ошибка при записи в файл");
        }
    }

    private void loadFromFile() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Ввод данных");
        dialog.setHeaderText("Пожалуйста, введите название файла:");
        dialog.setContentText("Название файла:");

        // Получаем результат ввода
        Optional<String> result = dialog.showAndWait();

        // Обрабатываем результат
        result.ifPresent(input -> {
            try {
                var path = Path.of(System.getProperty("user.dir"), "/%s".formatted(input));
                var newValue = this.userType.readValue(new InputStreamReader(Files.newInputStream(
                        path)));
                inputField.setText(newValue.toString());
                addElement();
                showInfo("Успешно Считано!", "Успешно!", "Значение будет добавлено в список");
            } catch (NumberFormatException e) {
                showError("Введите числовой индекс!");
            } catch (IOException e) {
                showError("Ошибка при чтении из файла");
            }
        });

    }

    // Обновить ListView
    private void refreshListView() {
        List<UserType> tmpList = new ArrayList<>();
        dataList.forEach(tmpList::add);
        listView.getItems().setAll(tmpList);
    }

    // Показать сообщение об ошибке
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }

    // Показать информационное сообщение
    private void showInfo(String message, String title, String header) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
