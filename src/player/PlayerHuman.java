package player;

import core.Player;
import core.communication_data.Position;

public class PlayerHuman extends Player {

    public PlayerHuman(String name, int playgroundSize) {
        super(name, playgroundSize);
    }

    @Override
    public Position makeTurn() {
        return null;
    }
}
