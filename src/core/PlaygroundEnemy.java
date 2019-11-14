package core;

import core.communication_data.Position;
import core.communication_data.TurnResult;

public class PlaygroundEnemy extends Playground {

    // TODO: is the ShipPool and Ship objects needed here? Or is everything done with fields

    public PlaygroundEnemy(int size) {
        super(size);
        this.resetFields(FieldType.FOG);
    }

    /**
     * Evaluate whether it is valid shoot position.
     *
     * @param position Shoot target
     * @return Error.None if field is fog and on the playground. false otherwise.
     */
    public TurnResult.Error canShootAt(Position position) {
        if (position.isOutsideOfPlayground(this.size))
            return TurnResult.Error.NOT_ON_PLAYGROUND;
        if (this.elements[position.getY()][position.getX()].type == FieldType.FOG)
            return TurnResult.Error.FIELD_ALREADY_DISCOVERED;
        return TurnResult.Error.NONE;
    }

    /**
     * Set a field to its proper type.
     * Called after shooting at the enemy and receiving the result.
     *
     * @param position Field that needs to be updated
     * @param type     new Type
     */
    public void updateField(Position position, FieldType type) {
        assert type == FieldType.WATER || type == FieldType.SHIP : "Update field to fog is nonsense";
        PlaygroundElement element;
        if (type == FieldType.SHIP) {
            element = this.getShipAtPosition(position.getX(), position.getY());
        } else {
            element = new WaterElement();
        }
        this.elements[position.getX()][position.getY()] = new Field(type, element, true);
    }

    private Ship getShipAtPosition(int x, int y) {
        return null;
    }
}
