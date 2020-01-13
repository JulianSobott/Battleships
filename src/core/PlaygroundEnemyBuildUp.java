package core;

import core.communication_data.*;

import java.beans.ConstructorProperties;

public class PlaygroundEnemyBuildUp extends PlaygroundBuildUp implements PlaygroundEnemy{

    public PlaygroundEnemyBuildUp(int size) {
        super(size);
        this.resetFields(FieldType.FOG);
    }

    @ConstructorProperties({"size", "numShipsFields", "numHitsShipsFields"})
    public PlaygroundEnemyBuildUp(int size, int numShipsFields, int numHitShipsFields) {
        super(size);
        this.numShipsFields = numShipsFields;
        this.numHitShipsFields = numHitShipsFields;
    }

    @Override
    public void update(ShotResult shotResult) {
        if (shotResult.getType() == FieldType.SHIP) {
            ShotResultShip res = ((ShotResultShip)shotResult);
            setShip(res.getPosition(), res.getStatus());
        } else {
            setWater(shotResult.getPosition());
        }
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
