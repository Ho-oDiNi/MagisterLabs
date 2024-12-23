package ru.atlasyk;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import ru.atlasyk.base.usertype.StringUserTypeImpl;
import ru.atlasyk.factory.UserFactory;
import ru.atlasyk.fraction.Fraction;
import ru.atlasyk.interfaces.UserType;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Создание выпадающего списка для выбора типа данных
        Label promptLabel = new Label("Выберите тип данных:");

        ComboBox<String> typeComboBox = new ComboBox<>();
        var userFactory = new UserFactory();
        var listTypes = userFactory.getTypeNameList();
        typeComboBox.getItems().addAll(listTypes);
        typeComboBox.setValue(listTypes.get(0)); // Устанавливаем значение по умолчанию

        // Кнопка для перехода к главной сцене
        Button goButton = new Button("Перейти к работе с данными");
        goButton.setOnAction(e -> {
            String selectedType = typeComboBox.getValue();
            UserType userType;

            // Определяем тип данных в зависимости от выбора
            if (StringUserTypeImpl.class.getSimpleName().equals(selectedType)) {
                userType = userFactory.getBuilderByName(StringUserTypeImpl.class.getSimpleName());
            } else {
                userType = userFactory.getBuilderByName(Fraction.class.getSimpleName()); // Пример для Fraction
            }
            openMainScene(primaryStage, userType);
        });

        // Размещение компонентов
        VBox layout = new VBox(10, promptLabel, typeComboBox, goButton);
        layout.setPadding(new Insets(10));

        // Создание сцены выбора типа
        Scene scene = new Scene(layout, 300, 200);
        primaryStage.setTitle("Выбор типа данных");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Переход к основной сцене с выбранным типом данных
    private void openMainScene(Stage primaryStage, UserType selectedType) {
        // Создание новой сцены для работы с данными
        MainScene mainScene = new MainScene(selectedType);
        mainScene.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }

}
