package player;

import core.Player;
import core.communication_data.Position;
import core.communication_data.ShotResult;
import core.communication_data.ShotResultWater;

import java.beans.ConstructorProperties;
import java.util.HashMap;

public class PlayerNetwork extends Player {

    @ConstructorProperties({"index", "name", "playgroundSize"})
    public PlayerNetwork(int index, String name, int playgroundSize) {
        super(index, name, playgroundSize);
    }

    // TODO: Add connection to class that can send/receive messages

    private final HashMap<String, Object> networkData = new HashMap<>();

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
        return networkData.containsKey("allShipsPlaced");
    }

    @Override
    public ShotResult gotHit(Position position) {
        // communicator.sendMessage("" + position);
        // waitForMessage type
        // return new ShotResultWater();
        return null;
    }

    public void setAllShipsPlaced() {
        networkData.put("allShipsPlaced", true);
    }

    public Position turnPosition;
}
