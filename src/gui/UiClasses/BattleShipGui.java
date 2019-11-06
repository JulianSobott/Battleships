package gui.UiClasses;

import java.io.Serializable;
import java.util.ArrayList;

public class BattleShipGui implements Serializable {

    private int shipID;
    private int shipSize;
    private ShipAlignment shipAlignment; // TODO: Take ShipPosition.Direction?
    private ArrayList shipPosition = new ArrayList(); //??? Notwenfid ??

    public BattleShipGui(int shipSize, ShipAlignment shipAlignment){

        this.shipSize = shipSize;
        this.shipAlignment =shipAlignment;
    }


    public BattleShipGui(int ShipID, int shipSize, ShipAlignment shipAlignment){

        this(shipSize, shipAlignment);
        this.shipID = ShipID;
    }

    public int getShipID() {
        return shipID;
    }

    public void setShipID(int shipID) {
        this.shipID = shipID;
    }

    public int getShipSize() {
        return shipSize;
    }

    public void setShipSize(int shipSize) {
        this.shipSize = shipSize;
    }

    public ShipAlignment getShipAlignment() {
        return shipAlignment;
    }

    public void setShipAlignment(ShipAlignment shipAlignment) {
        this.shipAlignment = shipAlignment;
    }
}
