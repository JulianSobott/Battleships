package core.playgrounds;

import core.Ship;
import core.WaterElement;
import core.communication_data.Position;

/**
 * playground where ships are added while playing
 */
public class PlaygroundBuildUp extends Playground {

    public PlaygroundBuildUp(int size) {
        super(size);
        this.resetFields(FieldType.FOG);
    }

    /**
     * Call when a WaterField is discovered
     * @param position Position of the WaterField
     */
    public void setWater(Position position) {
        this.elements[position.getY()][position.getX()] = new Field(FieldType.WATER, new WaterElement(), true);
    }

    /**
     * Call when a ship is discovered.
     *
     * Concatenates adjacent ship objects.
     * @param position ShipPosition of the discovered Ship
     * @param lifeStatus Status of the overall Ship object.
     */
    public void setShip(Position position, Ship.LifeStatus lifeStatus) {
        numHitShipsFields++;
        Ship newShip = new Ship(position);
        int[][] adjacentPositions = {{-1, 0}, {0, -1}, {1, 0}, {0, 1}};
        for (int[] adjacentPos : adjacentPositions) {
            int x = position.getX() + adjacentPos[0];
            int y = position.getY() + adjacentPos[1];
            if (x >= 0 && y >= 0) {
                Position p = new Position(x, y);
                if (!p.isOutsideOfPlayground(this.size) && this.elements[p.getY()][p.getX()].type == FieldType.SHIP) {
                    Ship adjacentShip = (Ship) this.elements[p.getY()][p.getX()].element;
                    this.removeShipByID(adjacentShip.getId());
                    adjacentShip.concatenateShips(newShip);
                    newShip = adjacentShip;
                    this.elements[p.getY()][p.getX()].element = newShip;
                }
            }
        }
        newShip.setLives(lifeStatus == Ship.LifeStatus.ALIVE ? 2 : 1); // Got hit will take lives later on
        this.elements[position.getY()][position.getX()] = new Field(FieldType.SHIP, newShip, true);
        this.putShip(newShip);
        if (lifeStatus == Ship.LifeStatus.SUNKEN) {
            for(Position p : getSurroundingWaterPositions(newShip)) {
                setWater(p);
            }
        }
    }
}
