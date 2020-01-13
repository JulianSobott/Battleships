package core;

import core.communication_data.Position;
import core.communication_data.ShotResult;
import core.communication_data.ShotResultShip;
import core.communication_data.ShotResultWater;
import core.utils.logging.LoggerLogic;

import java.util.HashSet;

public class PlaygroundOwnPlaceable extends PlaygroundPlaceable implements PlaygroundOwn {

    public PlaygroundOwnPlaceable(int size) {
        super(size);
    }

    /**
     *
     * @param position x, y coordinates of the shot.
     * @return A ShotResult, with information about what field was hit
     */
    @Override
    public ShotResult gotHit(Position position){
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

    /**
     * Get a list of all Positions around a ship that are water.
     * When a ship is sunken it is known, that these fields are water.
     *
     * @param s A ship. Most likely a ship that is sunken.
     * @return All positions around this ship
     */
    // TODO: maybe rewrite and move to other playground
    public Position[] getSurroundingWaterPositions(Ship s) {
        HashSet<Position> waterPositions = new HashSet<Position>();
        int[][] surroundingFields = {{-1, -1}, {-1, 0}, {0, -1}, {0, 0}, {0, 1}, {1, 0}, {1, 1}, {-1, 1}, {1, -1}};
        for (Position shipPosition : s.getShipPosition().generateIndices()) {
            for (int[] surrPos : surroundingFields) {
                int x = surrPos[0] + shipPosition.getX();
                int y = surrPos[1] + shipPosition.getY();
                if(!(x < 0 || y < 0)){
                    Position pos = new Position(x, y);
                    if (!pos.isOutsideOfPlayground(this.size) && this.elements[y][x].type == FieldType.WATER) {
                        waterPositions.add(pos);
                    }
                }
            }
        }
        Position[] positions = new Position[waterPositions.size()];
        return waterPositions.toArray(positions);
    }
}
