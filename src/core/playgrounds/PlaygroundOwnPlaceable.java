package core.playgrounds;

import core.Ship;
import core.communication_data.Position;
import core.communication_data.ShotResult;
import core.communication_data.ShotResultShip;
import core.communication_data.ShotResultWater;
import core.utils.logging.LoggerLogic;

public class PlaygroundOwnPlaceable extends PlaygroundPlaceable implements PlaygroundOwn {

    public PlaygroundOwnPlaceable() {
        // JSON Deserialization
    }

    public PlaygroundOwnPlaceable(int size) {
        super(size);
        this.resetFields(Playground.FieldType.WATER);
    }

    /**
     *
     * @param position x, y coordinates of the shot.
     * @return A ShotResult, with information about what field was hit
     */
    @Override
    public ShotResult gotHit(Position position){
        LoggerLogic.debug("gotHit: position=" + position);
        Playground.Field f = this.elements[position.getY()][position.getX()];
        f.gotHit();
        ShotResult res;
        if(f.type == Playground.FieldType.SHIP){
            Ship s = (Ship) f.element;
            if (s.getStatus() == Ship.LifeStatus.SUNKEN) {
                Position[] surroundingWaterPositions = this.getSurroundingWaterPositions(s);
                this.hitWaterFieldsAroundSunkenShip(surroundingWaterPositions);
                res = new ShotResultShip(position, Playground.FieldType.SHIP, s.getStatus(), surroundingWaterPositions, s.getShipPosition());
            } else {
                res = new ShotResultShip(position, Playground.FieldType.SHIP, s.getStatus());
            }
        }else{
            res = new ShotResultWater(position, f.type);
        }
        LoggerLogic.debug("gotHit return: ShotResult=" + res);
        return res;
    }

}
