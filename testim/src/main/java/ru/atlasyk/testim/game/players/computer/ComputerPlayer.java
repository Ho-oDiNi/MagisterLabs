package ru.atlasyk.testim.game.players.computer;

import ru.atlasyk.testim.game.elements.fields.GameField;
import ru.atlasyk.testim.game.elements.Point;
import ru.atlasyk.testim.game.elements.ships.Ship;
import ru.atlasyk.testim.game.elements.ships.ShipFactory;
import ru.atlasyk.testim.game.players.Player;

public class ComputerPlayer extends Player {

    private AIModule ai = new AIModule();


    public ComputerPlayer(String name) {
        super(name);
        getField().placeShips(ShipFactory.getRandomlyPlacedShips());
    }

    @Override
    public Point makeShot() {
        return ai.calculateCorrectShot();
    }

    @Override
    public void setInfoAboutLastShot(Point point, GameField.CellStatus shot, Ship ship, int maxSize) {

        ai.setInformationAboutLastShot(point, shot, ship, maxSize);
    }

}

