package gui.UiClasses;

import core.Ship;
import core.communication_data.ShipID;
import core.communication_data.ShipPosition;

import java.io.Serializable;
import java.util.ArrayList;

public class BattleShipGui implements Serializable {

    private ShipID shipID;
    private ShipPosition position;

    public BattleShipGui(int shipSize, ShipAlignment shipAlignment){
        this.position = ShipPosition.DEFAULT(shipSize);
    }


    public BattleShipGui(ShipID ShipID, int shipSize, ShipAlignment shipAlignment){
        this(shipSize, shipAlignment);
        this.shipID = ShipID;
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

    public int getShipSize() {
        return this.position.getLENGTH();
    }

    public Object getShipAlignment() {
        return ShipAlignment.Horizontal;
    }

    public void setShipAlignment(ShipAlignment vertical) {
    }
}
