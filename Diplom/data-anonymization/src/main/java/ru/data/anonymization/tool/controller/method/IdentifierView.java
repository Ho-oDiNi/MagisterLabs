package ru.data.anonymization.tool.controller.method;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.data.anonymization.tool.Methods.options.type.Identifier;
import ru.data.anonymization.tool.builder.DialogBuilder;
import ru.data.anonymization.tool.dto.Enum.ShowMode;
import ru.data.anonymization.tool.service.DepersonalizationService;
import ru.data.anonymization.tool.service.TableInfoService;
import ru.data.anonymization.tool.util.ComponentUtils;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class IdentifierView {
    private static final int MAX_LENGTH = 40;

    private final TableInfoService tableInfoService;
    private final DepersonalizationService depersonalizationService;

    @FXML
    private ListView<String> leftListView;
    @FXML
    private ListView<String> rightListView;
    @FXML
    private TextField newTableName;

    @FXML
    private TextField customName;
    @FXML
    private TextField tableName;
    @FXML
    private HBox buttonContainer;

    private Stage stage;

    public void configView(String title, String table, ShowMode mode, String name, VBox vBox) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(IdentifierView.class.getResource("identifier-view.fxml"));
        stage = ComponentUtils.modalStageView(fxmlLoader, "Настройка метода (" + title + ")", "gears.png");
        tableName.setText(table);

        List<String> columnList = tableInfoService.getColumnNames(table);

        Button saveButton = new Button("Сохранить");
        saveButton.setStyle("-fx-background-color: #4bbd50; -fx-text-fill: white;");
        saveButton.setOnAction(event -> saveAction(table, mode));
        buttonContainer.getChildren().add(saveButton);

        if (mode.equals(ShowMode.EDIT)) {
            customName.setDisable(true);
            customName.setText(name);

            Identifier dto = (Identifier) depersonalizationService.getMethod(name);

            List<String> selectedElement = dto.getColumn();
            columnList.removeAll(selectedElement);

            newTableName.setText(dto.getNewNameTable());

            ObservableList<String> leftItems = FXCollections.observableArrayList(columnList);
            leftListView.setItems(leftItems);

            ObservableList<String> rightItems = FXCollections.observableArrayList(selectedElement);
            rightListView.setItems(rightItems);

            Button deleteButton = new Button("Удалить");
            deleteButton.setStyle("-fx-background-color: #e83434; -fx-text-fill: white;");
            deleteButton.setOnAction(event -> deleteAction(name, vBox));
            buttonContainer.getChildren().add(deleteButton);
        } else {
            ObservableList<String> leftItems = FXCollections.observableArrayList(columnList);
            leftListView.setItems(leftItems);
        }

        stage.show();
    }

    @FXML
    private void moveToRight() {
        String selectedItem = leftListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            leftListView.getItems().remove(selectedItem);
            rightListView.getItems().add(selectedItem);
        }
    }

    @FXML
    private void moveToLeft() {
        String selectedItem = rightListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            rightListView.getItems().remove(selectedItem);
            leftListView.getItems().add(selectedItem);
        }
    }

    @FXML
    private void moveToRightClick(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() > 1) {
            moveToRight();
        }
    }

    @FXML
    private void moveToLeftClick(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() > 1) {
            moveToLeft();
        }
    }

    private void saveAction(String table, ShowMode mode) {
        List<String> columns = rightListView.getItems().stream().toList();

        if (customName.getText().isEmpty() || customName.getText().isBlank()) {
            DialogBuilder.createErrorDialog("Нужно ввести название!");
        } else if (columns.isEmpty()) {
            DialogBuilder.createErrorDialog("Нужно выбрать хотя бы один атрибут!");
        } else if (newTableName.getText().isEmpty() || newTableName.getText().isBlank()) {
            DialogBuilder.createErrorDialog("Нужно ввести имя новой таблицы!");
        } else {
            Identifier dto = new Identifier();
            dto.setNameTable(table);
            dto.setNamesColumn(columns);
            dto.setNewNameTable(newTableName.getText());
            if (mode.equals(ShowMode.EDIT) || !depersonalizationService.isContainsKey(customName.getText())) {
                String name = customName.getText().length() < MAX_LENGTH ? customName.getText() : customName.getText().substring(0, MAX_LENGTH);
                depersonalizationService.addMethod(name, dto);
                stage.close();
            } else {
                DialogBuilder.createErrorDialog("Такое название уже существует!");
            }
        }
    }

    private void deleteAction(String name, VBox vBox) {
        depersonalizationService.removeMethod(name);
        Node node = vBox.lookup("#" + name.replace(" ", ""));
        vBox.getChildren().remove(node);
        stage.close();
    }
}