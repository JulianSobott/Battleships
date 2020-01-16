package core.communication_data;

import core.playgrounds.Playground;

public class ShotResultWater extends ShotResult {

    public ShotResultWater(Position position, Playground.FieldType type) {
        super(position, type);
    }

    @Override
    public String toString() {
        return "ShotResultWater{} " + super.toString();
    }
}
