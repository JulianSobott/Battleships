package player;

import core.Player;
import core.communication_data.TurnResult;

public class PlayerNetwork extends Player {
    public PlayerNetwork(String name, int playgroundSize) {
        super(name, playgroundSize);
    }

    @Override
    public TurnResult makeTurn() {
        return null;
    }
}
