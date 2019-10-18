package core.communication_data;

import core.Playground;

public abstract class ShotResult {

    private final Playground.FieldType type;
    private final Position position;

    public ShotResult(Position position, Playground.FieldType type){
        this.position = position;
        this.type = type;
    }

    public Playground.FieldType getType() {
        return type;
    }

    public Position getPosition() {
        return position;
    }
}
