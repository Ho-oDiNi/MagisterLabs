package ru.atlasyk.testim.game.players.computer;

import ru.atlasyk.testim.game.elements.fields.GameField;
import ru.atlasyk.testim.game.elements.Point;
import ru.atlasyk.testim.game.elements.ships.Ship;

import java.util.*;

/**
 * Module for computer player - decide next correct shot
 * Shot ship to the death and analyze empty cells
 */

class AIModule {

    //Copy of already known player field (not information about ships, only about shots)
    private GameField playersField = new GameField();

    private Point lastShot; //shot from last turn
    private Point saveOfSuccessfulLastShot = null; //if shot was successful - save it
    private Ship currentShip = null; //current ship, we trying to kill

    private boolean onXLine = false; //what direction we going
    private boolean onYLine = false;

    private Point start = null; //start and end of ship
    private Point end = null;

    private boolean weGoingTop = true;

    private int maxSizedShip = 4; //current maximum ship of player

    //set info about shot and ship (if it was shot)
    void setInformationAboutLastShot(Point p, GameField.CellStatus status, Ship ship, int maxSize) {
        lastShot = p;
        playersField.setCellStatus(p, status);
        maxSizedShip = maxSize;

        if (currentShip == null) {
            currentShip = ship;
        }
        if (ship != null && ship.isSank()) {
            for (int i = 0; i < ship.getPointsAround().length; i++) {
                playersField.setCellStatus(ship.getPointsAround()[i], GameField.CellStatus.EMPTY_SHOT);
            }
        }
    }

    //determine next correct shot


    Point calculateCorrectShot() {
        if (currentShip == null || currentShip.isSank()) { //if ship is sank, or we dont have one
            return getRandomPoint();
        }

        if (onYLine) { //if we are already on the line
            return verticalWay();
        } else if (onXLine) {
            return horizontalWay();
        }

        if (saveOfSuccessfulLastShot != null && //determine the orientation of the ship (horizontal or vertical)
                playersField.getCellStatus(saveOfSuccessfulLastShot) == GameField.CellStatus.SHIP_SHOT &&
                playersField.getCellStatus(lastShot) == GameField.CellStatus.SHIP_SHOT
                && !lastShot.equals(saveOfSuccessfulLastShot)) {

            int dx = lastShot.getX() - saveOfSuccessfulLastShot.getX();
            if (dx > 0) {
                end = lastShot;
                start = saveOfSuccessfulLastShot;
                onXLine = true;
                return horizontalWay();
            } else if (dx < 0) {
                end = saveOfSuccessfulLastShot;
                start = lastShot;
                onXLine = true;
                return horizontalWay();
            }

            int dy = lastShot.getY() - saveOfSuccessfulLastShot.getY();
            if (dy > 0) {
                end = lastShot;
                start = saveOfSuccessfulLastShot;
                onYLine = true;
                return verticalWay();
            } else if (dy < 0) {
                end = saveOfSuccessfulLastShot;
                start = lastShot;
                onYLine = true;
                return verticalWay();
            }
        }

        if (playersField.getCellStatus(lastShot) == GameField.CellStatus.SHIP_SHOT) {//ship is shot
            saveOfSuccessfulLastShot = lastShot;
        }

        if (saveOfSuccessfulLastShot != null &&
                playersField.getCellStatus(saveOfSuccessfulLastShot) == GameField.CellStatus.SHIP_SHOT &&
                playersField.getCellStatus(lastShot) == GameField.CellStatus.EMPTY_SHOT) {
            //our guess was wrong
            lastShot = saveOfSuccessfulLastShot;
        }

        return getStartingDirection();
    }

    private Point getRandomPoint() {
        currentShip = null;
        saveOfSuccessfulLastShot = null;
        start = null;
        end = null;
        onXLine = false;
        onYLine = false;
        weGoingTop = true;

        Point[] possibleRet = getExpectablePoints();
        int e = new Random().nextInt(possibleRet.length);
        return possibleRet[e];
    }

    //shot for determine direction of ship (shooting around first shot point)
    private Point getStartingDirection() {
        Point p = null;
        do {
            int randomMove = new Random().nextInt(4);
            switch (randomMove) {
                case 0:
                    p = new Point(lastShot.getX() + 1, lastShot.getY());
                    break;
                case 1:
                    p = new Point(lastShot.getX(), lastShot.getY() + 1);
                    break;
                case 2:
                    p = new Point(lastShot.getX() - 1, lastShot.getY());
                    break;
                case 3:
                    p = new Point(lastShot.getX(), lastShot.getY() - 1);
            }
        } while (!isCellCorrectForShot(p));

        return p;
    }

    //for this methods saveOfSuccessfulLastShot is point for search to X or Y Line
    private Point horizontalWay() {
        if (playersField.getCellStatus(lastShot) == GameField.CellStatus.EMPTY_SHOT) {
            weGoingTop = false;
        }

        if (isCellCorrectForShot(new Point(end.getX() + 1, end.getY())) && weGoingTop) {
            end = new Point(end.getX() + 1, end.getY());
            return end;
        } else {
            weGoingTop = false;
        }

        if (isCellCorrectForShot(new Point(start.getX() - 1, start.getY()))) {
            start = new Point(start.getX() - 1, start.getY());
            return start;
        }
        return null;
    }

    private Point verticalWay() {
        if (playersField.getCellStatus(lastShot) == GameField.CellStatus.EMPTY_SHOT) {
            weGoingTop = false;
        }

        if (isCellCorrectForShot(new Point(end.getX(), end.getY() + 1)) && weGoingTop) {
            end = new Point(end.getX(), end.getY() + 1);
            return end;
        } else {
            weGoingTop = false;
        }

        if (isCellCorrectForShot(new Point(start.getX(), start.getY() - 1))) {
            start = new Point(start.getX(), start.getY() - 1);
            return start;
        }
        return null;
    }

    private boolean isCellCorrectForShot(Point p) {
        return p != null && p.getX() >= 0 && p.getX() < GameField.fieldSize
                && p.getY() >= 0 && p.getY() < GameField.fieldSize
                && playersField.getCellStatus(p) == GameField.CellStatus.EMPTY;
    }


    //returns only the points, that can fill current maximum player's ship
    private Point[] getExpectablePoints() {

        Point[][] tempField = new Point[GameField.fieldSize][GameField.fieldSize];
        for (int i = 0; i < tempField.length; i++) {
            for (int j = 0; j < tempField.length; j++) {
                tempField[i][j] = new Point(j, i);
            }
        }

        HashSet<Point> ret = new HashSet<>();
        for (int i = 0; i < GameField.fieldSize; i++) {
            for (int j = 0; j < GameField.fieldSize; j++) {
                if (playersField.getCellStatus(new Point(j, i)) == GameField.CellStatus.EMPTY) {
                    ret.addAll(pointsForGivenPoint(new Point(j, i), tempField));
                }
            }
        }

        Point[] retTrue = new Point[ret.size()];
        retTrue = ret.toArray(retTrue);

        return retTrue;
    }

    private HashSet<Point> pointsForGivenPoint(Point p, Point[][] tempField) {
        HashSet<Point> ret = new HashSet<>();

        boolean isRight = true;
        for (int i = 0; i < maxSizedShip; i++) {
            if (!isCellCorrectForShot(new Point(p.getX() + i, p.getY()))) {
                isRight = false;
                break;
            }
        }

        if (isRight) {
            ret.addAll(Arrays.asList(tempField[p.getY()]).subList(p.getX(), maxSizedShip + p.getX()));
        }

        isRight = true;
        for (int i = 0; i < maxSizedShip; i++) {
            if (!isCellCorrectForShot(new Point(p.getX(), p.getY() + i))) {
                isRight = false;
                break;
            }
        }

        if (isRight) {
            for (int i = 0; i < maxSizedShip; i++) {
                ret.add(tempField[p.getY() + i][p.getX()]);
            }
        }

        return ret;
    }


}
