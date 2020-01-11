package player;

import core.Player;
import core.Playground;
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
        return connected.getMakeTurnPosition();
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
        if (res.type == Playground.FieldType.WATER) {
            return new ShotResultWater(position, Playground.FieldType.WATER);
        } else {
            if (res.sunken) {
                // TODO: get WaterFields and ShipPosition
                return new ShotResultShip(position, Playground.FieldType.SHIP, Ship.LifeStatus.SUNKEN, null, null);
            }else {
                return new ShotResultShip(position, Playground.FieldType.SHIP, Ship.LifeStatus.ALIVE);
            }
        }
    }

    public void setAllShipsPlaced() {
        networkData.put("allShipsPlaced", true);
    }

    public Position turnPosition;
}
