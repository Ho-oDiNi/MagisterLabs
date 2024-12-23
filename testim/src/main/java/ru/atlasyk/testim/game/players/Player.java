package ru.atlasyk.testim.game.players;

import ru.atlasyk.testim.game.elements.fields.GameField;
import ru.atlasyk.testim.game.elements.Point;
import ru.atlasyk.testim.game.elements.ships.Ship;

/**
 * Abstract class for player
 */

public abstract class Player {

    private String name;

    private GameField field; //main field

    public Player(String name) {
        this.name = name;
        field = new GameField();
    }

    public String name() {
        return name;
    }

    public abstract Point makeShot();

    public GameField.CellStatus getCellStatus(Point point) {
        return field.getCellStatus(point);
    }

    public GameField getField() {
        return field;
    }

    //set status of shot on field and returns ship on given point (if it is in the cell)
    public Ship setCellStatus(Point point, GameField.CellStatus status) {
        field.setCellStatus(point, status);
        if (status == GameField.CellStatus.SHIP_SHOT) {
            Ship temp = field.getShip(point);
            temp.hitShip();

            if (temp.isSank()) {
                Point[] p = temp.getPointsAround();
                for (Point aP : p) {
                    setCellStatus(aP, GameField.CellStatus.EMPTY_SHOT);
                }
            }
            return temp;
        }
        return null;

    }

    public boolean allShipsDead() {
        return shipsAlive() == 0;
    }

    //returns how many ship is alive
    public int shipsAlive() {
        int ret = 10;
        for (Ship ship : field.getShips()) {
            if (ship.isSank()) {
                ret--;
            }
        }
        return ret;
    }

    public void setInfoAboutLastShot(Point point, GameField.CellStatus shot, Ship ship, int maxSize) {
    }

    public int maxSizeOfAliveShips() {
        int max = 1;

        for (Ship ship : field.getShips()) {
            if (!ship.isSank() && ship.getSize() > max) {
                max = ship.getSize();
            }
        }

        return max;
    }

    @Override
    public String toString() {
        return "\n" + name + "'s field\n" + field.toString();
    }

}

