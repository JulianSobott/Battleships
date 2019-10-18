package player;

import core.Player;
import core.communication_data.TurnResult;

public class PlayerAI extends Player {
    public PlayerAI(String name, int playgroundSize) {
        super(name, playgroundSize);
    }

    @Override
    public TurnResult makeTurn() {
        return null;
    }
}
