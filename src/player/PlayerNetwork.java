package player;

import core.Player;
import core.communication_data.Position;
import core.communication_data.ShotResult;

public class PlayerNetwork extends Player {
    public PlayerNetwork(String name, int playgroundSize) {
        super(name, playgroundSize);
    }

    @Override
    public Position makeTurn() {
        // TODO: wait till enemy turn and return
        return null;
    }

    @Override
    public ShotResult gotHit(Position position) {
        // TODO: Send message to network
        return null;
    }
}
