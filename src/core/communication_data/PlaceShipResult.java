package core.communication_data;

public class PlaceShipResult {

    private final boolean successfullyPlaced;
    private final ShipPosition position;
    private final ShipID shipID;
    private final Error ERROR;

    public enum Error{
        NONE, ID_NOT_EXIST, SPACE_TAKEN, NOT_ON_PLAYGROUND
    }
    private PlaceShipResult(boolean successfullyPlaced, ShipPosition position, ShipID shipID, Error error){
        this.successfullyPlaced = successfullyPlaced;
        this.position = position;
        this.shipID = shipID;
        this.ERROR = error;
    }

    public static PlaceShipResult failed(ShipPosition position, ShipID shipID, Error error){
        return new PlaceShipResult(false, position, shipID, error);
    }

    public static PlaceShipResult success(ShipPosition position, ShipID shipID){
        return new PlaceShipResult(true, position, shipID, Error.NONE);
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

    public Error getERROR() {
        return ERROR;
    }

    @Override
    public String toString() {
        return "PlaceShipResult{" +
                "successfullyPlaced=" + successfullyPlaced +
                ", position=" + position +
                ", shipID=" + shipID +
                ", ERROR=" + ERROR +
                '}';
    }
}
