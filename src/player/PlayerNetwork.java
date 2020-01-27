package player;

import core.Player;
import core.Ship;
import core.communication_data.Position;
import core.communication_data.ShotResult;
import core.playgrounds.Playground;
import core.playgrounds.PlaygroundEnemyBuildUp;
import core.playgrounds.PlaygroundOwnBuildUp;
import network.Connected;

import java.beans.ConstructorProperties;
import java.util.HashMap;

public class PlayerNetwork extends Player {

    private Connected connected;

    public PlayerNetwork(int index, String name, int playgroundSize, Connected connected) {
        super(index, name, playgroundSize);
        this.connected = connected;
        this.playgroundOwn = new PlaygroundOwnBuildUp(playgroundSize);
        this.playgroundEnemy = new PlaygroundEnemyBuildUp(playgroundSize);
    }

    @ConstructorProperties({"index", "name", "playgroundSize"})
    public PlayerNetwork(int index, String name, int playgroundSize) {
        this(index, name, playgroundSize, null);
    }

    private final HashMap<String, Object> networkData = new HashMap<>();

    @Override
    public Position makeTurn() {
        // received SHOT
        return connected.popMakeTurnPosition();
    }

    @Override
    public void update(ShotResult shotResult) {
        super.update(shotResult);
        connected.sendAnswer(shotResult);
    }

    public void sendSaveGame(long id) {
        connected.sendSaveGame(id);
    }

    @Override
    public boolean areAllShipsPlaced() {
        return networkData.containsKey("allShipsPlaced");
    }

    @Override
    public ShotResult gotHit(Position position) {
        // send SHOT and receive ANSWER
        connected.sendShot(position);
        Connected.ShotResTuple res = connected.getShotResult();
        // Update field element and type
        if (res.type == Playground.FieldType.WATER) {
            getPlaygroundOwn().setWater(position);
        } else {
            Ship.LifeStatus lifeStatus;
            if (res.sunken) {
                lifeStatus = Ship.LifeStatus.SUNKEN;
            }else {
                lifeStatus = Ship.LifeStatus.ALIVE;
            }
            getPlaygroundOwn().setShip(position, lifeStatus, true);
        }
        return super.gotHit(position);
    }

    public void setAllShipsPlaced() {
        networkData.put("allShipsPlaced", true);
    }

    public PlaygroundOwnBuildUp getPlaygroundOwn() {
        return (PlaygroundOwnBuildUp) this.playgroundOwn;
    }

    public void setConnected(Connected connected) {
        this.connected = connected;
    }
}
