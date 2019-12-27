package player;

import core.Player;
import core.communication_data.TurnResult;

public class PlayerHuman extends Player {

    public PlayerHuman(String name, int playgroundSize) {

        super(name, playgroundSize);
    }

    @Override
    public TurnResult makeTurn() {
        return null;
    }
}
