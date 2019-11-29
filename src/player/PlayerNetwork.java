package player;

import core.Player;
import core.communication_data.Position;

public class PlayerNetwork extends Player {
    public PlayerNetwork(String name, int playgroundSize) {
        super(name, playgroundSize);
    }

    @Override
    public Position makeTurn() {
        return null;
    }
}
