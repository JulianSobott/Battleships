package core.communication_data;

public class PlaceShipsRandomRes {

    private final ShipData[] shipData;
    private final boolean successfully;

    private PlaceShipsRandomRes(ShipData[] shipData, boolean successfully) {
        this.shipData = shipData;
        this.successfully = successfully;
    }

    public static PlaceShipsRandomRes success(ShipData[] shipData) {
        return new PlaceShipsRandomRes(shipData, true);
    }

    public static PlaceShipsRandomRes failure() {
        return new PlaceShipsRandomRes(null, false);
    }

    public ShipData[] getShipData() {
        return shipData;
    }

    public boolean isSuccessfully() {
        return successfully;
    }

    public static class ShipData {
        final ShipPosition POSITION;
        final ShipID id;

        public ShipData(ShipPosition POSITION, ShipID id) {
            this.POSITION = POSITION;
            this.id = id;
        }

        public ShipPosition getPOSITION() {
            return POSITION;
        }

        public ShipID getId() {
            return id;
        }
    }
}
