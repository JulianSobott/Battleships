package core;

import core.communication_data.Position;
import core.communication_data.ShipList;

/**
 * playground where ships are added while playing
 */
public class PlaygroundBuildUp extends Playground {

    public PlaygroundBuildUp(int size) {
        super(size);
        this.resetFields(FieldType.FOG);
    }

    public void hitWater(Position position) {
        this.elements[position.getY()][position.getX()] = new Field(FieldType.WATER, new WaterElement(), true);
    }

    public void hitShip(Position position, Ship.LifeStatus lifeStatus) {
        numHitShipsFields++;
        Ship newShip = new Ship(position);
        int[][] adjacentPositions = {{-1, 0}, {0, -1}, {1, 0}, {0, 1}};
        for (int[] adjacentPos : adjacentPositions) {
            Position p = new Position(adjacentPos[0], adjacentPos[1]);
            if (!p.isOutsideOfPlayground(this.size) && this.elements[p.getY()][p.getX()].type == FieldType.SHIP) {
                Ship adjacentShip = (Ship) this.elements[p.getY()][p.getX()].element;
                newShip = Ship.concatenateShips(newShip, adjacentShip);
                this.elements[p.getY()][p.getX()].element = newShip;
            }
        }
        newShip.setLives(lifeStatus == Ship.LifeStatus.ALIVE ? 1 : 0);
        this.elements[position.getY()][position.getX()].element = newShip;
        this.elements[position.getY()][position.getX()] = new Field(FieldType.SHIP, newShip, true);
    }

    public boolean areAllShipsSunken(){
        return this.numShipsFields == this.numHitShipsFields;
    }

}
