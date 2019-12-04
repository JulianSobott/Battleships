package player;

import core.Player;
import core.communication_data.Position;
import core.utils.logging.LoggerLogic;

import java.util.Random;

public class PlayerAI extends Player {
    public PlayerAI(String name, int playgroundSize) {
        super(name, playgroundSize);
        this.playgroundOwn.placeShipsRandom();
        this.playgroundOwn.printField();
    }

    @Override
    public Position makeTurn() {
        Random r =  new Random();
        int x = r.nextInt(this.playgroundOwn.getSize());
        int y = r.nextInt(this.playgroundOwn.getSize());
        Position pos = new Position(x, y);
        LoggerLogic.info("PlayerAI.makeTurn: position=" + pos);
        return pos;
    }
}
