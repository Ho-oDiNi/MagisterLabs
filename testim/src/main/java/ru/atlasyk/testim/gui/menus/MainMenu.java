package ru.atlasyk.testim.gui.menus;

import ru.atlasyk.testim.game.Game;
import ru.atlasyk.testim.game.elements.fields.GameField;
import ru.atlasyk.testim.game.elements.Point;
import ru.atlasyk.testim.game.players.computer.ComputerPlayer;
import ru.atlasyk.testim.game.players.human.HumanPlayer;
import ru.atlasyk.testim.gui.fields.FieldCreator;
import ru.atlasyk.testim.gui.fields.MainField;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainMenu {

    private TextField inputNickname = new TextField("nickname");

    public void createMainMenu(Stage primaryStage) {
        VBox vb = new VBox(10);
        vb.setPadding(new Insets(10));
        vb.setAlignment(Pos.CENTER);
        primaryStage.getIcons().add(new Image(getClass().getResource("/img/style/cursor.png")
                                                        .toExternalForm()));

        Scene scene = new Scene(vb);
        scene.getStylesheets().setAll(getClass().getResource("/styles/modena.css").toExternalForm());
        primaryStage.setWidth(235);
        primaryStage.setHeight(200);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Sea Battle");

        Label inf_lab = new Label("Nickname: ");
        inf_lab.setMinWidth(60);
        HBox hb1 = new HBox(5, inf_lab, inputNickname);

        Button createGameVsComputerButton = new Button("Play VS computer");
        createGameVsComputerButton.setAlignment(Pos.CENTER);
        createGameVsComputerButton.setPrefWidth(Double.MAX_VALUE);
        createGameVsComputerButton.setOnAction(event -> createVSComputerGame());

        inputNickname.textProperty().addListener((observable, oldValue, newValue) -> {
            createGameVsComputerButton.setDisable(newValue.isEmpty() || newValue.length() < 3);
        });

        vb.getChildren().addAll(hb1, new Separator(), createGameVsComputerButton);
    }

    private void findRoomWindow() {
        Stage w = new Stage();
        w.initModality(Modality.APPLICATION_MODAL);
        VBox vb = new VBox(10);
        vb.setPadding(new Insets(10));
        vb.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vb);
        scene.getStylesheets().setAll(getClass().getResource("/styles/modena.css")
                                                .toExternalForm());

        w.setWidth(240);
        w.setHeight(120);
        w.setScene(scene);
        w.setResizable(false);
        w.setTitle("Sea Battle");

        Label inf_lab = new Label("Room ID: ");
        inf_lab.setMinWidth(60);
        TextField input = new TextField();
        HBox hb1 = new HBox(5, inf_lab, input);

        Button findGameButton = new Button("Search");
        findGameButton.setAlignment(Pos.CENTER);

        vb.getChildren().addAll(hb1, findGameButton);

        w.show();
    }

    private void createVSComputerGame() {
        HumanPlayer human = new HumanPlayer(inputNickname.getText());

        new FieldCreator(human).showAndWait();

        if (human.getCellStatus(new Point(0, 0)) == GameField.CellStatus.SHIP_SHOT) {
            return;
        }

        Game mGame = new Game(human, new ComputerPlayer("Computer"));

        MainField playerField = new MainField(mGame);
        playerField.show();

    }

}