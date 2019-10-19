package core.communication_data;

class PlaceShipResult {

    private final boolean successfullyPlaced;
    private final ShipPosition position;

    public PlaceShipResult(boolean successfullyPlaced, ShipPosition position) {
        this.successfullyPlaced = successfullyPlaced;
        this.position = position;
    }

    public boolean isSuccessfullyPlaced() {
        return successfullyPlaced;
    }

    public ShipPosition getPosition() {
        return position;
    }
}
