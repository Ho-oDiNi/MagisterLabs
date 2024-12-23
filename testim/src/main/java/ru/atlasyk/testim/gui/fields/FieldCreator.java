package ru.atlasyk.testim.gui.fields;

import ru.atlasyk.testim.game.elements.fields.GameField;
import ru.atlasyk.testim.game.elements.fields.GameFieldForCreator;
import ru.atlasyk.testim.game.elements.Point;
import ru.atlasyk.testim.game.elements.ships.Ship;
import ru.atlasyk.testim.game.elements.ships.ShipFactory;
import ru.atlasyk.testim.game.players.Player;
import ru.atlasyk.testim.gui.elements.ShipItem;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Iterator;

/**
 * Class provides window with interface to create
 * user's field with user's placement of ships.
 * Also user can place ships randomly.
 */

public class FieldCreator extends Stage {

    private GameFieldForCreator field;
    private Player player;

    private Button[][] playerField = new Button[GameField.fieldSize][GameField.fieldSize];

    private FlowPane pane = null;
    private GridPane grid = null;

    public FieldCreator(Player player) {
        field = new GameFieldForCreator();
        this.player = player;
        setResizable(false);
        initModality(Modality.APPLICATION_MODAL);
        getIcons().add(new Image(getClass().getResource("/img/style/cursor.png").toExternalForm()));
        setTitle(player.name() + "'s field setup");


        HBox hb = new HBox();
        hb.getChildren().addAll(setUpPlayerField(), new Separator(Orientation.VERTICAL), setUpRightPart());

        Scene scene = new Scene(hb);
        scene.getStylesheets().add(getClass().getResource("/styles/modena.css").toExternalForm());
        setScene(scene);

//        label that, ship placement was canceled (need in main menu)
        setOnCloseRequest(event -> player.getField().setCellStatus(new Point(0, 0), GameField.CellStatus.SHIP_SHOT));

    }

    private GridPane setUpPlayerField() {
        grid = new GridPane();
        grid.getStylesheets().add(getClass().getResource("/styles/PlayerField.css").toExternalForm());
        Image ima = new Image(getClass().getResource("/img/style/sea.gif").toExternalForm());
        grid.setBackground(new Background(new BackgroundImage(ima, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));

        for (int i = 0; i < GameField.fieldSize; i++) {
            for (int j = 0; j < GameField.fieldSize; j++) {
                playerField[i][j] = makeCell(i, j);
                grid.add(playerField[i][j], j, i);
            }
        }

        return grid;
    }

    private Button makeCell(int i, int j) {
        Button cell = new Button("");

        setUpDroppingInCell(i, j, cell);
        setUpDragingInCell(cell);

        cell.setMinSize(28, 28);

        return cell;
    }

    private void setUpDragingInCell(Button cell) {
        cell.setOnDragOver((DragEvent event) -> {
            String id = event.getDragboard().getString();
            ShipItem itemInPane = pane.lookup("#" + id) != null
                    ? (ShipItem) pane.lookup("#" + id)
                    : (ShipItem) grid.lookup("#" + id);

            if (itemInPane != null) {
                int direction = itemInPane.getDirection();
                int size = itemInPane.getSize();

                int x = GridPane.getColumnIndex(cell);
                int y = GridPane.getRowIndex(cell);

                if (event.getDragboard().hasString() && field.isPlaceCorrectForShip(new Point(x, y), size, direction)) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
            }
            event.consume();
        });
    }

    private void setUpDroppingInCell(int i, int j, Button cell) {
        cell.setOnDragDropped((DragEvent event) -> {
            Dragboard db = event.getDragboard();

            String id = db.getString();

            ShipItem itemInPane = (ShipItem) pane.lookup("#" + id);
            ShipItem itemInGrid = (ShipItem) grid.lookup("#" + id);

            if (itemInPane != null) {
                int direction = itemInPane.getDirection();
                int size = itemInPane.getSize();

                Ship ship = ShipFactory.createShip(new Point(j, i), size, direction);

                field.placeTempShip(ship);

                grid.add(itemInPane, j, i, direction == 1 ? size : 1, direction == 2 ? size : 1);
                pane.getChildren().remove(itemInPane);
            }

            if (itemInGrid != null) {
                int direction = itemInGrid.getDirection();
                int size = itemInGrid.getSize();

                int x = GridPane.getColumnIndex(itemInGrid);
                int y = GridPane.getRowIndex(itemInGrid);

                field.removeTempShip(new Point(x, y));

                Ship ship = ShipFactory.createShip(new Point(j, i), size, direction);

                field.placeTempShip(ship);

                grid.getChildren().remove(itemInGrid);
                grid.add(itemInGrid, j, i, direction == 1 ? size : 1, direction == 2 ? size : 1);
            }

            updateFieldGraphic();

            event.setDropCompleted(true);
            event.consume();
        });
    }

    private VBox setUpRightPart() {
        VBox vb = new VBox();
//        vb.setPrefWidth(400);

        pane = new FlowPane();
        VBox.setVgrow(pane, Priority.ALWAYS);
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(10);
        pane.setVgap(10);

        for (int i = 0, size = 4; i < 10; i++) {
            if (i == 1 || i == 3 || i == 6) {
                size--;
            }
            ShipItem imv = makeShipItem(i, size);
            Tooltip tip = new Tooltip("Right click to rotate");
            tip.setShowDelay(new Duration(1000));
            Tooltip.install(imv, tip);

            pane.getChildren().add(imv);
        }

        pane.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasString()) {
                String id = db.getString();

                ShipItem temp = (ShipItem) grid.lookup("#" + id);
                if (temp != null) {
                    int x = GridPane.getColumnIndex(temp);
                    int y = GridPane.getRowIndex(temp);

                    grid.getChildren().remove(temp);
                    field.removeTempShip(new Point(x, y));

                    pane.getChildren().add(temp);
                }
            }
            updateFieldGraphic();
            event.setDropCompleted(true);
            event.consume();
        });

        pane.setOnDragOver((DragEvent event) -> {
            if (event.getGestureSource() != pane &&
                    event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        Button generateRandom = new Button("Random");
        generateRandom.setOnAction(event -> {
            clearPlayerField();
            for (Ship ship : ShipFactory.getRandomlyPlacedShips()) {
                field.placeTempShip(ship);
            }
            drawShips();
            updateFieldGraphic();
        });


        Button clear = new Button("Clear Field");
        clear.setOnAction(event -> {
            clearPlayerField();
            updateFieldGraphic();
        });

        Label lab = new Label();
        lab.setMinWidth(145);
        lab.setAlignment(Pos.CENTER_RIGHT);

        Button ok = new Button("Start game");
        ok.setOnAction(event -> {
            if (field.getShips() == null || field.getShips().length != 10) {
                lab.setText("Place all ships!");
                return;
            }

            player.getField().placeShips(field.getShips());
            close();
        });

        HBox hb = new HBox(generateRandom, clear, lab, ok);
        hb.setSpacing(10);
        hb.setPadding(new Insets(8));

        Label label = new Label("Drag ships into the field. Right click on the ship to rotate it.");
        label.setPadding(new Insets(5));
        label.setAlignment(Pos.CENTER);


        vb.getChildren().addAll(label, new Separator(), pane, new Separator(), hb);


        return vb;
    }

    private ShipItem makeShipItem(int index, int size) {
        ShipItem shipItem = new ShipItem(getClass().getResource("/img/ships/").toExternalForm() + size + ".png", 1, size);
        shipItem.setId(this.getClass().getSimpleName() + size + index);

        shipItem.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                if (grid.lookup("#" + shipItem.getId()) != null) {
                    int direction = shipItem.getDirection() == 1 ? 2 : 1;

                    int x = GridPane.getColumnIndex(shipItem);
                    int y = GridPane.getRowIndex(shipItem);
                    Ship retShip = field.getShip(new Point(x, y));
                    field.removeTempShip(new Point(x, y));

                    if (field.isPlaceCorrectForShip(new Point(x, y), size, direction)) {
                        Ship ship = ShipFactory.createShip(new Point(x, y), size, direction);

                        field.placeTempShip(ship);

                        grid.getChildren().remove(shipItem);

                        shipItem.rotate();

                        grid.add(shipItem, x, y, direction == 1 ? size : 1, direction == 2 ? size : 1);

                        updateFieldGraphic();

                    } else {
                        field.placeTempShip(retShip);
                    }

                } else {
                    shipItem.rotate();
                }
            }

            event.consume();
        });
        return shipItem;
    }

    private void clearPlayerField() {
        if (grid.getChildren() != null) {
            Iterator<Node> i = grid.getChildren().iterator();
            while (i.hasNext()) {
                Node n = i.next();

                if (n instanceof ShipItem) {
                    i.remove();
                    pane.getChildren().add(n);
                    if (((ShipItem) n).getDirection() == 2) {
                        ((ShipItem) n).rotate();
                    }
                }
            }
            i = pane.getChildren().iterator();
            while (i.hasNext()) {
                Node n = i.next();

                if (n instanceof ShipItem) {
                    if (((ShipItem) n).getDirection() == 2) {
                        ((ShipItem) n).rotate();
                    }
                }
            }

        }
        field.resetTempField();
    }

    private void updateFieldGraphic() {
        for (int i = 0; i < GameField.fieldSize; i++) {
            for (int j = 0; j < GameField.fieldSize; j++) {
                GameField.CellStatus status = field.getCellStatus(new Point(j, i));
                switch (status) {
                    case EMPTY:
                        playerField[i][j].setStyle("-fx-background-color: transparent;");
                        break;
                    case SHIP:
                        playerField[i][j].setStyle("-fx-background-color: rgba(255,255,0,0.3);");
                        break;
                    case EMPTY_SHOT:
                        playerField[i][j].setStyle("-fx-background-color: rgba(255,255,255,0.3);");
                }
            }
        }
    }


    private void drawShips() {
        Ship[] ships = field.getShips();


        for (Ship shep : ships) {
            int size = shep.getSize();
            int direction = shep.getOrientation();


            ShipItem it = null;


            for (Node n : pane.getChildren()) {
                if (n instanceof ShipItem && n.getId().contains("FieldCreator" + size)) {
                    it = (ShipItem) n;
                }

            }

            if (direction == 2 || direction == 4 && (it != null ? it.getDirection() : 0) == 1) {
                assert it != null;
                it.rotate();
            }

            if (it != null) {
                Point one = shep.getPoints()[0];
                Point two = shep.getPoints()[shep.getPoints().length - 1];

                Point fin = new Point(one.getX() < two.getX() ? one.getX() : two.getX(),
                        one.getY() < two.getY() ? one.getY() : two.getY());

                grid.add(it, fin.getX(), fin.getY(), it.getDirection() == 1 ? size : 1, it.getDirection() == 2 ? size : 1);
                pane.getChildren().remove(it);
            }
        }


    }
}

