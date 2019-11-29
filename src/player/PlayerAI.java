package player;

import core.Player;
import core.communication_data.Position;
import core.utils.Logger;

import java.util.Random;

public class PlayerAI extends Player {
    public PlayerAI(String name, int playgroundSize) {
        super(name, playgroundSize);
        Logger.debug("FIELD AI");
        this.playgroundOwn.placeShipsRandom();
    }

    @Override
    public Position makeTurn() {
        Random r =  new Random();
        int x = r.nextInt(this.playgroundOwn.getSize());
        int y = r.nextInt(this.playgroundOwn.getSize());
        Logger.debug("Pos", x, y);
        return new Position(x, y);
    }
}
