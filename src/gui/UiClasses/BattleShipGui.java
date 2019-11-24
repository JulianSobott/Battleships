package gui.UiClasses;

import core.communication_data.ShipID;
import core.communication_data.ShipPosition;

import java.io.Serializable;

public class BattleShipGui implements Serializable {

    private ShipID shipID;
    private ShipPosition position;

    public BattleShipGui(int shipSize){
        this.position = ShipPosition.DEFAULT(shipSize);
    }


    public BattleShipGui(ShipID shipID, ShipPosition shipPosition){
        this.shipID = shipID;
        this.position = shipPosition;
    }

    public ShipID getShipID() {
        return shipID;
    }

    public void setShipID(ShipID shipID) {
        this.shipID = shipID;
    }

    public ShipPosition getPosition() {
        return position;
    }

    public void setPosition(ShipPosition position) {
        this.position = position;
    }

}
