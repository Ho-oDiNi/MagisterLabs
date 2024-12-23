package ru.atlasyk.testim.game.elements.fields;

import ru.atlasyk.testim.game.elements.Point;
import ru.atlasyk.testim.game.elements.ships.Ship;

/**
 * This class represent a game field - main container, which
 * include cells and ships.
 * Also class is needed for creating user's field in GUI with FieldCreator
 */

public class GameField {

    public static final int fieldSize = 15;

    //  States in which every cell can be
    public enum CellStatus {
        EMPTY_SHOT, EMPTY, SHIP_SHOT, SHIP
    }

    //  Cells
    CellStatus[][] cells = new CellStatus[GameField.fieldSize][GameField.fieldSize];

    //Ships on this field
    Ship[] ships = null;


    public GameField() {
        for (int i = 0; i < fieldSize; i++) {
            for (int j = 0; j < fieldSize; j++) {
                cells[i][j] = CellStatus.EMPTY; //init cells with empty
            }
        }
    }

    public Ship[] getShips() {
        return ships;
    }

    //return status of cell in given point
    public CellStatus getCellStatus(Point p) {
        return cells[p.getY()][p.getX()];
    }

    public void setCellStatus(Point point, CellStatus status) {
        cells[point.getY()][point.getX()] = status;
    }

    //get ship, if ship is placed in this point
    public Ship getShip(Point point) {
        for (Ship ship : ships) {
            if (ship.contains(point)) {
                return ship;
            }
        }
        return null;
    }

    //final placement of all ships on the field
    //after this method field is ready to battle
    public void placeShips(Ship[] ships) {
        this.ships = ships;
        for (Ship ship : this.ships) {
            for (Point point : ship.getPoints()) {
                setCellStatus(point, CellStatus.SHIP);
            }
        }
    }


    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < fieldSize; i++) {
            for (int j = 0; j < fieldSize; j++) {
                char c = cells[i][j] == CellStatus.EMPTY ?
                        '_' :
                        cells[i][j] == CellStatus.SHIP ?
                                '#' :
                                cells[i][j] == CellStatus.EMPTY_SHOT ?
                                        'o' :
                                        'X';
                str.append(c).append(" ");
            }
            str.append('\n');
        }
        return str.toString();
    }
}


