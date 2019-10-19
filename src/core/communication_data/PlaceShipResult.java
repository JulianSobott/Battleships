package core.communication_data;

public class PlaceShipResult {

    private final boolean successfullyPlaced;
    private final ShipPosition position;
    private final ShipID shipID;

    public PlaceShipResult(boolean successfullyPlaced, ShipPosition position, ShipID shipID) {
        this.successfullyPlaced = successfullyPlaced;
        this.position = position;
        this.shipID = shipID;
    }

    public boolean isSuccessfullyPlaced() {
        return successfullyPlaced;
    }

    public ShipPosition getPosition() {
        return position;
    }

    public ShipID getShipID() {
        return shipID;
    }
}
