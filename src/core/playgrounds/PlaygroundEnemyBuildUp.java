package core.playgrounds;

import core.communication_data.Position;
import core.communication_data.ShotResult;
import core.communication_data.ShotResultShip;
import core.communication_data.TurnResult;

import java.beans.ConstructorProperties;

/**
 * Playground where ships and water is added while playing.
 * Used, when at game it is not known where the ships are placed, which is for the enemy always the case.
 */
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

    /**
     * @param shotResult Result with information what was hit
     */
    @Override
    public void update(ShotResult shotResult) {
        if (shotResult.getType() == FieldType.SHIP) {
            ShotResultShip res = ((ShotResultShip)shotResult);
            setShip(res.getPosition(), res.getStatus(), false);
        } else {
            setWater(shotResult.getPosition());
        }
    }

    /**
     * @param position Position where to shoot.
     * @return Possible errors, why it is an invalid position.
     */
    @Override
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
