package core;

import core.communication_data.Position;
import core.communication_data.ShipList;
import core.communication_data.ShipPosition;
import core.communication_data.TurnResult;
import core.utils.logging.LoggerLogic;

import java.beans.ConstructorProperties;

public class PlaygroundEnemy extends PlaygroundBuildUp{

    public PlaygroundEnemy(int size) {
        super(size);
    }

    @ConstructorProperties({"size", "numShipsFields", "numHitsShipsFields"})
    public PlaygroundEnemy(int size, int numShipsFields, int numHitShipsFields) {
        super(size);
        this.numShipsFields = numShipsFields;
        this.numHitShipsFields = numHitShipsFields;
    }

    private Ship getShipAtPosition(int x, int y){
        return (Ship) this.elements[y][x].element;
    }


    public TurnResult.Error canShootAt(Position position) {
        if (position.isOutsideOfPlayground(this.size))
            return TurnResult.Error.NOT_ON_PLAYGROUND;
        if (this.elements[position.getY()][position.getX()].type != FieldType.FOG)
            return TurnResult.Error.FIELD_ALREADY_DISCOVERED;
        else
            assert !this.elements[position.getY()][position.getX()].isHit() : "When field was hit it can not be fog";
        return TurnResult.Error.NONE;
    }


    public int getNumShipsFields() {
        return numShipsFields;
    }

    public int getNumHitShipsFields() {
        return numHitShipsFields;
    }
}
