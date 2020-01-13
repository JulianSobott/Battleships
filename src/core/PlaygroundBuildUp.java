package core;

import core.communication_data.Position;
import core.communication_data.ShipList;

/**
 * playground where ships are added while playing
 */
public class PlaygroundBuildUp extends Playground {

    protected int numShipsFields = 0;
    protected int numHitShipsFields = 0;

    public PlaygroundBuildUp(int size) {
        super(size);
        this.resetFields(FieldType.FOG);

        ShipList l = ShipList.fromSize(size);
        for(ShipList.Pair p : l){
            this.numShipsFields += p.getSize() * p.getNum();
        }
    }

    public void setHitWaterField(Position position) {
        this.elements[position.getY()][position.getX()] = new Field(FieldType.WATER, new WaterElement(), true);
    }

    public void setHitShipField(Position position, Ship.LifeStatus lifeStatus) {
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
    }

    public boolean areAllShipsSunken(){
        return this.numShipsFields == this.numHitShipsFields;
    }

}
