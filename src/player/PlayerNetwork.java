package player;

import core.Player;
import core.communication_data.Position;
import core.communication_data.ShotResult;

public class PlayerNetwork extends Player {
    public PlayerNetwork(int index, String name, int playgroundSize) {
        super(index, name, playgroundSize);
    }

    // TODO: Add connection to class that can send/receive messages

    @Override
    public Position makeTurn() {
        // TODO: wait till enemy turn and return
        return null;
    }

    @Override
    public boolean areAllShipsPlaced() {
        // TODO
        return true;
    }

    @Override
    public ShotResult gotHit(Position position) {
        // TODO: Send message to network
        return null;
    }
}
