package core;

import core.communication_data.Position;
import core.communication_data.ShipList;
import core.communication_data.ShipPosition;
import core.communication_data.TurnResult;
import core.utils.logging.LoggerLogic;

import java.beans.ConstructorProperties;

public class PlaygroundEnemy extends Playground{

    private int numShipsFields = 0;
    private int numHitShipsFields = 0;

    public PlaygroundEnemy(int size) {
        super(size);
        this.resetFields(FieldType.FOG);

        ShipList l = ShipList.fromSize(size);
        for(ShipList.Pair p : l){
            this.numShipsFields += p.getSize() * p.getNum();
        }
    }

    @ConstructorProperties({"size", "numShipsFields", "numHitsShipsFields"})
    public PlaygroundEnemy(int size, int numShipsFields, int numHitShipsFields) {
        super(size);
        this.numShipsFields = numShipsFields;
        this.numHitShipsFields = numHitShipsFields;
    }

    public void updateField(Position position, FieldType type){
        // TODO: Handle sunken ships
        PlaygroundElement element;
        if(type == FieldType.SHIP){
            this.numHitShipsFields++;
            this.updateShipObjects(position);
            element = this.getShipAtPosition(position.getX(), position.getY());
        }else {
            element = new WaterElement();
        }
        this.elements[position.getY()][position.getX()] = new Field(type, element, true);
    }

    private Ship getShipAtPosition(int x, int y){
        return (Ship) this.elements[y][x].element;
    }

    public boolean areAllShipsSunken(){
        return this.numShipsFields == this.numHitShipsFields;
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

    /**
     * A new ship was discovered at Position.
     * Build new Object if no adjacent fields are also ships.
     * If an adjacent Field is a ship add them to one object.
     * @param position Position of the newly discovered Ship
     */
    public void updateShipObjects(Position position) {
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
        this.elements[position.getY()][position.getX()].element = newShip;
    }


    public int getNumShipsFields() {
        return numShipsFields;
    }

    public int getNumHitShipsFields() {
        return numHitShipsFields;
    }
}
