package ru.atlasyk.testim.game.elements.ships;

import ru.atlasyk.testim.game.elements.Point;

/**
 * Class for ship and it's properties
 */

public class Ship {

    //Points, where ship is placed
    private Point[] points;

    //Points, that surround ship
    private Point[] pointsAround;

    //How many deck is shot
    private int numberOfAlreadyShot = 0;

    //Size of ship
    private int size;

    //Orientation of ship (1. right, 2. down, 3. left, 4. up)
    private int orientation;

    void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public int getOrientation() {
        return orientation;
    }

    Ship(int size) {
        this.size = size;
        points = new Point[size];
    }

    public Point[] getPointsAround() {
        return pointsAround;
    }

    void setPointsAround(Point[] pointsAround) {
        this.pointsAround = pointsAround;
    }

    void setPoints(Point[] points) {
        this.points = points;
    }

    public Point[] getPoints() {
        return points;
    }

    public int getSize() {
        return size;
    }

    //Return true, if ship's points contain given point
    public boolean contains(Point point) {
        for (Point point_ : points) {
            if (point_.equals(point)) {
                return true;
            }
        }
        return false;
    }

    //Return true, if ship is sank
    public boolean isSank() {
        return numberOfAlreadyShot == size;
    }

    //Hit ship
    public void hitShip() {
        numberOfAlreadyShot++;
    }


}