package core.communication_data;

import core.Playground;
import core.Ship;

public class ShotResultShip extends ShotResult {

    private final Ship.LifeStatus status;

    public ShotResultShip(Position position, Playground.FieldType type, Ship.LifeStatus status) {
        super(position, type);
        this.status = status;
    }

    public Ship.LifeStatus getStatus(){
        return status;
    }
}
