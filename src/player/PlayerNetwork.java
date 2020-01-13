package player;

import core.Player;
import core.Playground;
import core.PlaygroundBuildUp;
import core.Ship;
import core.communication_data.Position;
import core.communication_data.ShotResult;
import core.communication_data.ShotResultShip;
import core.communication_data.ShotResultWater;
import network.Connected;

import java.beans.ConstructorProperties;
import java.util.HashMap;

public class PlayerNetwork extends Player {

    private Connected connected;

    @ConstructorProperties({"index", "name", "playgroundSize"})
    public PlayerNetwork(int index, String name, int playgroundSize, Connected connected) {
        super(index, name, playgroundSize);
        this.connected = connected;
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
        String num;
        if (shotResult.getType() == Playground.FieldType.SHIP) {
            if (((ShotResultShip) shotResult).getStatus() == Ship.LifeStatus.SUNKEN) {
                num = "2";
            } else {
                num = "1";
            }
        } else {
            num = "0";
        }
        connected.sendMessage("answer " + num);
    }

    @Override
    public boolean areAllShipsPlaced() {
        return networkData.containsKey("allShipsPlaced");
    }

    @Override
    public ShotResult gotHit(Position position) {

        // send SHOT and receive ANSWER
        connected.sendMessage("shot " + position.getX() + " " + position.getY());
        Connected.ShotResTuple res = connected.getShotResult();
        // Update field element and type
        if (res.type == Playground.FieldType.WATER) {
            this.playgroundOwn.setHitWaterField(position);
        } else {
            Ship.LifeStatus lifeStatus;
            if (res.sunken) {
                lifeStatus = Ship.LifeStatus.SUNKEN;
            }else {
                lifeStatus = Ship.LifeStatus.ALIVE;
            }
            this.playgroundOwn.setHitShipField(position, lifeStatus);
        }
        return super.gotHit(position);
    }

    public void setAllShipsPlaced() {
        networkData.put("allShipsPlaced", true);
    }

    public Position turnPosition;
}
