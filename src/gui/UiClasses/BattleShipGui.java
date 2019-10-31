package gui.UiClasses;

import java.util.ArrayList;

public class BattleShipGui {

    private int ShipID;
    private int ShipSize;
    private ArrayList shipPosition = new ArrayList();

    public BattleShipGui(String s, int ShipID, int ShipSize){

        this.ShipID = ShipID;
        this.ShipSize = ShipSize;

    }

    public int getShipID() {
        return ShipID;
    }

    public void setShipID(int shipID) {
        ShipID = shipID;
    }

    public int getShipSize() {
        return ShipSize;
    }

    public void setShipSize(int shipSize) {
        ShipSize = shipSize;
    }


}
