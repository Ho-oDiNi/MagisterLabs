package ru.atlasyk.testim.game.elements.fields;

import ru.atlasyk.testim.game.elements.Point;
import ru.atlasyk.testim.game.elements.ships.Ship;

import java.util.ArrayList;


/**
 * Next class is needed for FieldCreator in which user build custom field with ships.
 * these functions are responsible for creating "temporary"
 * field, in which "temp ships" will be located
 * On temp field EMPTYSHOTS cells represents cells, that forbidden for other ships.
 * After placing all ships you can get them from temp field
 * using getShips()
 */
public class GameFieldForCreator extends GameField {


    //list for temporary storing of ships, while creating field in FieldCreator
    private ArrayList<Ship> tempShips = new ArrayList<>();

    //function that needed while creating field in FieldCreator
    public void placeTempShip(Ship ship) {
        tempShips.add(ship);

        ships = new Ship[tempShips.size()];
        ships = tempShips.toArray(ships);

        updateTempField();
    }

    //function to delete tempShip in given point
    public void removeTempShip(Point point) {
        removeTempShip(getShip(point));
    }


    private void removeTempShip(Ship ship) {
        if (ship == null) {
            return;
        }
        tempShips.remove(ship);
        System.out.println();
        ships = new Ship[tempShips.size()];
        ships = tempShips.toArray(ships);

        updateTempField();
    }

    //update cells' statuses while creating user's field
    private void updateTempField() {
        for (int i = 0; i < fieldSize; i++) {
            for (int j = 0; j < fieldSize; j++) {
                cells[i][j] = CellStatus.EMPTY;
            }
        }

        if (ships != null) {
            for (Ship ship : ships) {
                for (Point point : ship.getPoints()) {
                    setCellStatus(point, CellStatus.SHIP);
                }
                for (Point point : ship.getPointsAround()) {
                    setCellStatus(point, CellStatus.EMPTY_SHOT);
                }
            }
        }
    }

    public void resetTempField() {
        ships = null;
        tempShips.clear();
        updateTempField();
    }

    //for FieldCreator
    public boolean isPlaceCorrectForShip(Point firstPoint, int size, int direction) {
        for (int i = 0; i < size; i++) {
            CellStatus status;
            switch (direction) {
                case 1:
                    if (firstPoint.getX() + size <= fieldSize) {
                        status = cells[firstPoint.getY()][firstPoint.getX() + i];
                        if (status != CellStatus.EMPTY) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                    break;
                case 2:
                    if (firstPoint.getY() + size <= fieldSize) {
                        status = cells[firstPoint.getY() + i][firstPoint.getX()];
                        if (status != CellStatus.EMPTY) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                    break;
            }
        }
        return true;
    }
}

