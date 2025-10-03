package com.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private LinkedList<?> currentList;
    private ComboBox<String> typeComboBox;
    private TextArea outputArea;
    private TextField inputField;
    private TextField indexField;
    private Button addButton, getButton, insertButton, removeButton;
    private Button saveTextButton, loadTextButton, saveBinaryButton, loadBinaryButton;
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Односвязный список");
        
        // Создание элементов управления
        typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll("Целые числа", "Дроби");
        typeComboBox.setValue("Целые числа");
        
        outputArea = new TextArea();
        outputArea.setEditable(false);
        
        inputField = new TextField();
        inputField.setPromptText("Значение");
        
        indexField = new TextField();
        indexField.setPromptText("Индекс");
        
        addButton = new Button("Добавить");
        getButton = new Button("Получить");
        insertButton = new Button("Вставить");
        removeButton = new Button("Удалить");
        
        saveTextButton = new Button("Сохранить текст");
        loadTextButton = new Button("Загрузить текст");
        saveBinaryButton = new Button("Сохранить бинарный");
        loadBinaryButton = new Button("Загрузить бинарный");
        
        // Настройка layout
        GridPane inputGrid = new GridPane();
        inputGrid.setHgap(10);
        inputGrid.setVgap(10);
        inputGrid.add(new Label("Тип данных:"), 0, 0);
        inputGrid.add(typeComboBox, 1, 0);
        inputGrid.add(new Label("Ввод:"), 0, 1);
        inputGrid.add(inputField, 1, 1);
        inputGrid.add(new Label("Индекс:"), 0, 2);
        inputGrid.add(indexField, 1, 2);
        
        HBox buttonBox = new HBox(10, addButton, getButton, insertButton, removeButton);
        HBox fileButtonBox = new HBox(10, saveTextButton, loadTextButton, saveBinaryButton, loadBinaryButton);
        
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        root.getChildren().addAll(inputGrid, buttonBox, outputArea, fileButtonBox);
        
        // Инициализация списка
        initializeList();
        
        // Обработчики событий
        typeComboBox.setOnAction(e -> initializeList());
        
        addButton.setOnAction(e -> addItem());
        getButton.setOnAction(e -> getItem());
        insertButton.setOnAction(e -> insertItem());
        removeButton.setOnAction(e -> removeItem());
        
        saveTextButton.setOnAction(e -> saveToTextFile());
        loadTextButton.setOnAction(e -> loadFromTextFile());
        saveBinaryButton.setOnAction(e -> saveToBinaryFile());
        loadBinaryButton.setOnAction(e -> loadFromBinaryFile());
        
        // Отображение окна
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }
    
    private void initializeList() {
        String type = typeComboBox.getValue();
        if ("Целые числа".equals(type)) {
            currentList = new LinkedList<Integer>(Integer.class);
        } else {
            currentList = new LinkedList<Fraction>(Fraction.class);
        }
        updateOutput();
    }
    
    private void addItem() {
        try {
            String input = inputField.getText();
            if ("Целые числа".equals(typeComboBox.getValue())) {
                ((LinkedList<Integer>)currentList).add(Integer.parseInt(input));
            } else {
                ((LinkedList<Fraction>)currentList).add(Fraction.parseFromString(input));
            }
            updateOutput();
            inputField.clear();
        } catch (Exception e) {
            showError("Ошибка добавления: " + e.getMessage());
        }
    }
    
    private void getItem() {
        try {
            int index = Integer.parseInt(indexField.getText());
            if ("Целые числа".equals(typeComboBox.getValue())) {
                int value = ((LinkedList<Integer>)currentList).get(index);
                outputArea.appendText("Значение по индексу " + index + ": " + value + "\n");
            } else {
                Fraction value = ((LinkedList<Fraction>)currentList).get(index);
                outputArea.appendText("Значение по индексу " + index + ": " + value + "\n");
            }
        } catch (Exception e) {
            showError("Ошибка получения: " + e.getMessage());
        }
    }
    
    private void insertItem() {
        try {
            int index = Integer.parseInt(indexField.getText());
            String input = inputField.getText();
            if ("Целые числа".equals(typeComboBox.getValue())) {
                ((LinkedList<Integer>)currentList).insert(index, Integer.parseInt(input));
            } else {
                ((LinkedList<Fraction>)currentList).insert(index, Fraction.parseFromString(input));
            }
            updateOutput();
            inputField.clear();
        } catch (Exception e) {
            showError("Ошибка вставки: " + e.getMessage());
        }
    }
    
    private void removeItem() {
        try {
            int index = Integer.parseInt(indexField.getText());
            if ("Целые числа".equals(typeComboBox.getValue())) {
                int removed = ((LinkedList<Integer>)currentList).remove(index);
                outputArea.appendText("Удалено: " + removed + "\n");
            } else {
                Fraction removed = ((LinkedList<Fraction>)currentList).remove(index);
                outputArea.appendText("Удалено: " + removed + "\n");
            }
            updateOutput();
        } catch (Exception e) {
            showError("Ошибка удаления: " + e.getMessage());
        }
    }
    
    private void saveToTextFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить как текстовый файл");
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                if ("Целые числа".equals(typeComboBox.getValue())) {
                    ((LinkedList<Integer>)currentList).saveToTextFile(file.getAbsolutePath());
                } else {
                    ((LinkedList<Fraction>)currentList).saveToTextFile(file.getAbsolutePath());
                }
                outputArea.appendText("Список сохранен в текстовый файл\n");
            } catch (IOException e) {
                showError("Ошибка сохранения: " + e.getMessage());
            }
        }
    }
    
    private void loadFromTextFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Загрузить из текстового файла");
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                if ("Целые числа".equals(typeComboBox.getValue())) {
                    ((LinkedList<Integer>)currentList).loadFromTextFile(file.getAbsolutePath());
                } else {
                    ((LinkedList<Fraction>)currentList).loadFromTextFile(file.getAbsolutePath());
                }
                updateOutput();
                outputArea.appendText("Список загружен из текстового файла\n");
            } catch (IOException e) {
                showError("Ошибка загрузки: " + e.getMessage());
            }
        }
    }
    
    private void saveToBinaryFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить как бинарный файл");
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                if ("Целые числа".equals(typeComboBox.getValue())) {
                    ((LinkedList<Integer>)currentList).saveToBinaryFile(file.getAbsolutePath());
                } else {
                    ((LinkedList<Fraction>)currentList).saveToBinaryFile(file.getAbsolutePath());
                }
                outputArea.appendText("Список сохранен в бинарный файл\n");
            } catch (IOException e) {
                showError("Ошибка сохранения: " + e.getMessage());
            }
        }
    }
    
    private void loadFromBinaryFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Загрузить из бинарного файла");
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                if ("Целые числа".equals(typeComboBox.getValue())) {
                    currentList = LinkedList.loadFromBinaryFile(file.getAbsolutePath(), Integer.class);
                } else {
                    currentList = LinkedList.loadFromBinaryFile(file.getAbsolutePath(), Fraction.class);
                }
                updateOutput();
                outputArea.appendText("Список загружен из бинарного файла\n");
            } catch (Exception e) {
                showError("Ошибка загрузки: " + e.getMessage());
            }
        }
    }
    
    private void updateOutput() {
        outputArea.setText(currentList.toString());
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}