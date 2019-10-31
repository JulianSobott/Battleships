package gui.UiClasses;

import javafx.scene.image.Image;

public class BattleShipImage extends Image {

    private int ShipID;
    private int ShipSize;

    public BattleShipImage(String s, int ShipID, int ShipSize){
        super(s);
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
