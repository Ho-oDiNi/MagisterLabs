package ru.atlasyk.testim.gui.fields;

import ru.atlasyk.testim.game.Game;
import ru.atlasyk.testim.game.elements.fields.GameField;
import ru.atlasyk.testim.game.elements.Point;
import ru.atlasyk.testim.game.elements.ships.Ship;
import ru.atlasyk.testim.game.players.Player;
import ru.atlasyk.testim.game.elements.ships.Ship;
import ru.atlasyk.testim.gui.elements.ShipItem;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;


/**
 * Class provides window with main menu
 */

public class MainField extends Stage {

    private Button[][] opponentField = new Button[GameField.fieldSize][GameField.fieldSize];
    private Button[][] playerField = new Button[GameField.fieldSize][GameField.fieldSize];

    private Game game;

    private VBox opponentPart;

    private Label statusBar;

    private Label numberOfOpponentShips;
    private Label numberOfPlayerShips;

    private static final int cellSize = 28;

    public MainField(Game game) {
        this.game = game;
        initModality(Modality.APPLICATION_MODAL);
        setResizable(false);
        getIcons().add(new Image(getClass().getResource("/img/style/cursor.png").toExternalForm()));
        setTitle("Battleship");

        HBox node = new HBox();
        Separator sep = new Separator(Orientation.VERTICAL);
        sep.setMinWidth(20);
        opponentPart = setUpOpponentField();
        VBox playerPart = setUpPlayerField();
        node.setPadding(new Insets(5));
        node.getChildren().addAll(opponentPart, sep, playerPart);

        statusBar = new Label("Your Turn");
        statusBar.setMinWidth(60);
        HBox node2 = new HBox();
        node2.setMinHeight(20);
        node2.getChildren().addAll(new Label("   Status: "), statusBar);

        numberOfOpponentShips = new Label("10");
        numberOfOpponentShips.setMinWidth(159);
        numberOfPlayerShips = new Label("10");
        HBox node3 = new HBox();
        node3.setMinHeight(20);
        node3.getChildren().addAll(new Label("   Alive opponents ships: "), numberOfOpponentShips, new Separator(Orientation.VERTICAL), new Label("   Your ships alive: "), numberOfPlayerShips);

        VBox mainNode = new VBox();
        mainNode.getChildren().addAll(node, new Separator(), node3, new Separator(), node2);

        Scene scene = new Scene(mainNode);
        scene.getStylesheets().setAll(getClass().getResource("/styles/modena.css").toExternalForm());
        setScene(scene);
    }

    private void updateFields() {
        for (int i = 0; i < GameField.fieldSize; i++) {
            for (int j = 0; j < GameField.fieldSize; j++) {
                GameField.CellStatus status = game.getOpponent().getCellStatus(new Point(j, i));

                if (status == GameField.CellStatus.SHIP_SHOT) {
                    if (game.getOpponent().getField().getShip(new Point(j, i)).isSank()) {
                        opponentField[i][j].setStyle("-fx-background-color:transparent;-fx-background-image:url(resources/img/cell/fire.gif);");
                    } else {
                        opponentField[i][j].setStyle("-fx-background-color:black;-fx-background-image:url(resources/img/cell/fire.gif);");
                    }
                    opponentField[i][j].setDisable(true);
                } else if (status == GameField.CellStatus.EMPTY_SHOT) {
                    opponentField[i][j].setStyle("-fx-background-color: transparent;");
                    opponentField[i][j].setDisable(true);
                }

                status = game.getPlayer().getCellStatus(new Point(j, i));

                if (status == GameField.CellStatus.SHIP_SHOT) {
                    playerField[i][j].setStyle("-fx-background-image:url(resources/img/cell/fire.gif);");
                } else if (status == GameField.CellStatus.EMPTY_SHOT) {
                    playerField[i][j].setStyle("-fx-background-color: rgba(255,255,255,0.5);");
                }
            }
        }

        numberOfPlayerShips.setText(String.valueOf(game.getPlayer().shipsAlive()));
        numberOfOpponentShips.setText(String.valueOf(game.getOpponent().shipsAlive()));
    }

    private void receiveShot() {

        Point point = game.receiveShotFromOpponent();
        statusBar.setText("Opponent Turn");

        Task task = new Task() {
            @Override
            protected Object call() {
                opponentPart.setDisable(true);

                try {
                    Thread.sleep(/*new Random().nextInt(2000) + 500*/100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                opponentPart.setDisable(false);
                return null;
            }
        };
        task.setOnSucceeded(event -> {
            updateFields();

            if (winnerIsFound()) return;

            GameField.CellStatus status = game.getPlayer().getCellStatus(point);

            if (status == GameField.CellStatus.SHIP_SHOT) {
                receiveShot();
            }
            statusBar.setText("Your Turn");
        });

        new Thread(task).start();
    }

    private boolean winnerIsFound() {
        if (game.getWinner() != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Congrats!");
            alert.setHeaderText(null);

            alert.setContentText(game.getWinner().name() + " won!");
            alert.showAndWait();
            close();
            return true;
        }
        return false;
    }

    private VBox setUpPlayerField() {

        VBox vb = new VBox(4);

        Label lb1 = new Label("  Your field: " + game.getPlayer().name());
        lb1.setMinHeight(20);
        vb.getStylesheets().add(getClass().getResource("/styles/PlayerField.css").toExternalForm());

        GridPane grid = new GridPane();
        Image ima = new Image(getClass().getResource("/img/style/sea.gif").toExternalForm());
        grid.setBackground(new Background(new BackgroundImage(ima, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));

        drawShip(grid, game.getPlayer());
        for (int i = 0; i < GameField.fieldSize; i++) {
            for (int j = 0; j < GameField.fieldSize; j++) {
                Button but =
                        playerField[i][j] = new Button(" ");
                but.setMinSize(cellSize, cellSize);

                but.setDisable(true);
                grid.add(but, j, i);
            }
        }
        vb.getChildren().addAll(lb1, grid);
        return vb;
    }

    private VBox setUpOpponentField() {

        VBox vb = new VBox(4);
        vb.getStylesheets().add(getClass().getResource("/styles/OpponentField.css").toExternalForm());
        Label lb1 = new Label("  Opponent field: " + game.getOpponent().name());
        lb1.setMinHeight(20);
        lb1.setAlignment(Pos.CENTER);

        GridPane grid = new GridPane();
        Image ima = new Image(getClass().getResource("/img/style/sea.gif").toExternalForm());
        grid.setBackground(new Background(new BackgroundImage(ima, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
        Image im = new Image(getClass().getResource("/img/style/cursor.png").toExternalForm());

        drawShip(grid, game.getOpponent());
        for (int i = 0; i < GameField.fieldSize; i++) {
            for (int j = 0; j < GameField.fieldSize; j++) {
                Button but =
                        opponentField[i][j] = new Button(" ");
                but.setFocusTraversable(false);
                but.setMinSize(cellSize, cellSize);
                but.setCursor(new ImageCursor(im, im.getWidth() / 2, im.getHeight() / 2));
                int finalI = i;
                int finalJ = j;

                but.setOnAction(event -> {

                    this.game.sendShotToOpponent(new Point(finalJ, finalI));
                    updateFields();

                    if (winnerIsFound()) return;

                    GameField.CellStatus status = this.game.getOpponent().getCellStatus(new Point(finalJ, finalI));

                    if (status != GameField.CellStatus.SHIP_SHOT) {
                        receiveShot();
                    }
                });

                grid.add(but, j, i);
            }

        }
        vb.getChildren().addAll(lb1, grid);
        return vb;
    }

    private void drawShip(GridPane grid, Player player) {
        Ship[] ships = player.getField().getShips();

        for (Ship shep : ships) {
            int size = shep.getSize();
            int direction = shep.getOrientation();
            ShipItem it = new ShipItem(getClass().getResource("/img/ships/").toExternalForm() + size + ".png", (direction == 1 || direction == 3) ? 1 : 2, size);
            if (it.getDirection() == 2) {
                it.rotate();
                it.setDirection(2);
            }
            Point one = shep.getPoints()[0];
            Point two = shep.getPoints()[shep.getPoints().length - 1];

            Point fin = new Point(one.getX() < two.getX() ? one.getX() : two.getX(),
                    one.getY() < two.getY() ? one.getY() : two.getY());

            grid.add(it, fin.getX(), fin.getY(), it.getDirection() == 1 ? size : 1, it.getDirection() == 2 ? size : 1);
        }
    }

}