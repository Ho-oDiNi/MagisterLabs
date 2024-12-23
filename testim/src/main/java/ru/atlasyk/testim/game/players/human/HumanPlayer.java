package ru.atlasyk.testim.game.players.human;

import ru.atlasyk.testim.game.elements.Point;
import ru.atlasyk.testim.game.players.Player;

public class HumanPlayer extends Player {


    public HumanPlayer(String text) {
        super(text);

    }

    @Override
    public Point makeShot() {
        return null;
    }

}
