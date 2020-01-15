package core;

import core.communication_data.Position;
import core.communication_data.ShotResult;
import core.communication_data.ShotResultShip;
import core.communication_data.ShotResultWater;
import core.utils.logging.LoggerLogic;

public class PlaygroundOwnBuildUp extends PlaygroundBuildUp implements PlaygroundOwn {

    public PlaygroundOwnBuildUp(int size) {
        super(size);
        this.resetFields(FieldType.WATER);

    }

    @Override
    public ShotResult gotHit(Position position) {
        LoggerLogic.info("gotHit: position=" + position);
        Field f = this.elements[position.getY()][position.getX()];
        f.gotHit();
        ShotResult res;
        if(f.type == FieldType.SHIP){
            Ship s = (Ship) f.element;
            if (s.getStatus() == Ship.LifeStatus.SUNKEN) {
                Position[] surroundingWaterPositions = this.getSurroundingWaterPositions(s);
                this.hitWaterFieldsAroundSunkenShip(surroundingWaterPositions);
                res = new ShotResultShip(position, FieldType.SHIP, s.getStatus(), surroundingWaterPositions, s.getShipPosition());
            } else {
                res = new ShotResultShip(position, FieldType.SHIP, s.getStatus());
            }
        }else{
            res = new ShotResultWater(position, f.type);
        }
        LoggerLogic.info("gotHit return: ShotResult=" + res);
        return res;
    }
}
