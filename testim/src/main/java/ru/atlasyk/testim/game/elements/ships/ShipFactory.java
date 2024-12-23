package ru.atlasyk.testim.game.elements.ships;

import ru.atlasyk.testim.game.elements.Point;
import ru.atlasyk.testim.game.elements.fields.GameField;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Class for producing randomly placed ships
 * <p>
 * Also class provide 1methods for filling ship's points and surroundings points.
 */


public class ShipFactory {

    //temporary set of ships
    private static Ship[] tempShips = new Ship[10];

    //temporary field
    private static GameField.CellStatus[][] tempField = new GameField.CellStatus[GameField.fieldSize][GameField.fieldSize];

    //Returns full set of correctly placed ships, that placed randomly on the field
    public static Ship[] getRandomlyPlacedShips() {
        clearField();

        for (int i = 0, size = 4; i < 10; i++) {
            if (i == 1 || i == 3 || i == 6) {
                size--;
            }
            tempShips[i] = new Ship(size);
            placeShip(tempShips[i]);
        }

        return tempShips;
    }

    //returns ship in a given position
    public static Ship createShip(Point firstPoint, int size, int direction) {
        Ship ship = new Ship(size);
        clearField();
        calculatePointsForShip(firstPoint, ship, direction);
        return ship;
    }

    //place ship with given size
    private static void placeShip(Ship ship) {

        int shipDirection; //1 - right, 2 - bot, 3 - left, 4 - top
        Point firstPoint;
        do {
            firstPoint = new Point(new Random().nextInt(GameField.fieldSize), new Random().nextInt(GameField.fieldSize));
            shipDirection = getCorrectDirectionForShip(firstPoint, ship.getSize(), new Random().nextInt(4) + 1);
        }
        while (shipDirection == 0);

        calculatePointsForShip(firstPoint, ship, shipDirection);

    }

    //clear field from the ships
    private static void clearField() {
        for (int i = 0; i < tempField.length; i++) {
            for (int j = 0; j < tempField.length; j++) {
                tempField[i][j] = GameField.CellStatus.EMPTY;
            }
        }
    }

    //Determine is ship can be placed in the firstPoint
    //Return 0 if not, otherwise - direction
    private static int getCorrectDirectionForShip(Point firstPoint, int size, int direction) {
        boolean t = true;

        for (int i = 0; i < size; i++) {
            GameField.CellStatus status;
            switch (direction) {
                case 1:
                    if (firstPoint.getX() + size <= GameField.fieldSize) {
                        status = tempField[firstPoint.getY()][firstPoint.getX() + i];
                        if (status != GameField.CellStatus.EMPTY) {
                            t = false;
                        }
                    } else {
                        t = false;
                    }
                    break;
                case 2:
                    if (firstPoint.getY() + size <= GameField.fieldSize) {
                        status = tempField[firstPoint.getY() + i][firstPoint.getX()];
                        if (status != GameField.CellStatus.EMPTY) {
                            t = false;
                        }
                    } else {
                        t = false;
                    }
                    break;
                case 3:
                    if (firstPoint.getX() - size >= -1) {
                        status = tempField[firstPoint.getY()][firstPoint.getX() - i];
                        if (status != GameField.CellStatus.EMPTY) {
                            t = false;
                        }
                    } else {
                        t = false;
                    }
                    break;
                case 4:
                    if (firstPoint.getY() - size >= -1) {
                        status = tempField[firstPoint.getY() - i][firstPoint.getX()];
                        if (status != GameField.CellStatus.EMPTY) {
                            t = false;
                        }
                    } else {
                        t = false;
                    }
                    break;
            }
        }
        return t ? direction : 0;
    }

    //Calculating correct points for a given ship,
    // from given point with given direction
    private static void calculatePointsForShip(Point firstPoint, Ship ship, int shipDirection) {

        Point[] shipPoints = new Point[ship.getSize()];
        Point[] pointsAroundShip;

        for (int i = 0; i < ship.getSize(); i++) {
            Point point = null;
            switch (shipDirection) {
                case 1:
                    point = new Point(firstPoint.getX() + i, firstPoint.getY());
                    break;
                case 2:
                    point = new Point(firstPoint.getX(), firstPoint.getY() + i);
                    break;
                case 3:
                    point = new Point(firstPoint.getX() - i, firstPoint.getY());
                    break;
                case 4:
                    point = new Point(firstPoint.getX(), firstPoint.getY() - i);
                    break;
            }
            assert point != null;
            tempField[point.getY()][point.getX()] = GameField.CellStatus.SHIP;
            shipPoints[i] = point;
        }

        ship.setPoints(shipPoints);
        pointsAroundShip = getPointsAroundShip(ship);
        ship.setPointsAround(pointsAroundShip);
        ship.setOrientation(shipDirection);

        for (Point aPointsAroundShip : pointsAroundShip) {
            tempField[aPointsAroundShip.getY()][aPointsAroundShip.getX()] = GameField.CellStatus.EMPTY_SHOT;
        }
    }

    //return correct surrounding point
    private static Point[] getPointsAroundShip(Ship ship) {
        List<Point> p = new ArrayList<>();
        for (Point point : ship.getPoints()) {
            p.addAll(pointsAroundPoint(point));
        }
        Point[] retPoints = new Point[p.size()];
        retPoints = p.toArray(retPoints);

        return retPoints;
    }

    //calculate free points around given point
    private static List<Point> pointsAroundPoint(Point point) {
        List<Point> temp = new ArrayList<>();

        for (int i = point.getY() - 1; i <= point.getY() + 1; i++) {
            for (int j = point.getX() - 1; j <= point.getX() + 1; j++) {
                Point arP = new Point(j, i);
                if (isPointCorrect(arP)) {
                    temp.add(arP);
                }
            }
        }

        return temp;
    }


    private static boolean isPointCorrect(Point p) {
        return p.getX() >= 0 && p.getY() >= 0
                && p.getX() < GameField.fieldSize && p.getY() < GameField.fieldSize
                && (tempField[p.getY()][p.getX()] != GameField.CellStatus.SHIP);
    }
}
