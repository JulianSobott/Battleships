package core.communication_data;

import core.playgrounds.Playground;
import core.Ship;

import java.util.Arrays;

public class ShotResultShip extends ShotResult {

    private final Ship.LifeStatus status;
    private final Position[] waterFields;
    private final ShipPosition shipPosition;

    public ShotResultShip(Position position, Playground.FieldType type, Ship.LifeStatus status,
                          Position[] waterFields, ShipPosition shipPosition) {
        super(position, type);
        assert (waterFields != null && status == Ship.LifeStatus.SUNKEN) ||
                (waterFields == null && status != Ship.LifeStatus.SUNKEN) : "Set waterFields only when ship is sunken";
        this.status = status;
        this.waterFields = waterFields;
        this.shipPosition = shipPosition;
    }

    public ShotResultShip(Position position, Playground.FieldType type, Ship.LifeStatus status) {
        this(position, type, status, null, null);
    }


    public Ship.LifeStatus getStatus() {
        return status;
    }

    public Position[] getWaterFields() {
        return waterFields;
    }

    @Override
    public String toString() {
        return "ShotResultShip{" +
                "status=" + status +
                ", waterFields=" + Arrays.toString(waterFields) +
                "} " + super.toString();
    }

    public ShipPosition getShipPosition() {
        return shipPosition;
    }
}
