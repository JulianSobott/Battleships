package player;

import core.Player;
import core.communication_data.Position;

public class PlayerHuman extends Player {

    public PlayerHuman(int index, String name, int playgroundSize) {
        super(index, name, playgroundSize);
    }

    @Override
    public Position makeTurn() {
        return null;
    }
}
