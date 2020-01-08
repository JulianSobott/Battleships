package player;

import core.Player;
import core.communication_data.Position;
import core.communication_data.ShotResult;
import core.communication_data.ShotResultWater;

import java.beans.ConstructorProperties;

public class PlayerNetwork extends Player {

    @ConstructorProperties({"index", "name", "playgroundSize"})
    public PlayerNetwork(int index, String name, int playgroundSize) {
        super(index, name, playgroundSize);
    }

    // TODO: Add connection to class that can send/receive messages

    @Override
    public Position makeTurn() {
        // TODO: wait till enemy turn and return
        return null;
    }

    public void sendResult(ShotResult shotResult) {

        // communicator.sendMessage("" + shotResult.getType())
    }

    @Override
    public boolean areAllShipsPlaced() {
        // TODO
        return true;
    }

    @Override
    public ShotResult gotHit(Position position) {
        // communicator.sendMessage("" + position);
        // waitForMessage type
        // return new ShotResultWater();
        return null;
    }

    public boolean allShipsPlaced;
    public Position turnPosition;
}
