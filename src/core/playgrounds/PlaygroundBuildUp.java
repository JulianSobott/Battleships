package core.playgrounds;

import core.Ship;
import core.WaterElement;
import core.communication_data.Position;

/**
 * playground where ships are added while playing
 */
public class PlaygroundBuildUp extends Playground {

    public PlaygroundBuildUp() {
    }

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
     * Organizes ships, that the Playground contains proper ship objects
     *
     * <h3>Algorithm</h3>
     * <pre>{@code
     * create a new ship at this position
     * for adjacentField in adjacentFields:
     *      if adjacentField is Ship:
     *          concatenate with newShip
     * if not concatenated:
     *      add newShip
     * }</pre>
     *
     * Concatenates adjacent ship objects.
     * @param position ShipPosition of the discovered Ship
     * @param lifeStatus Status of the overall Ship object.
     * @param hitLater true, when the method {@link PlaygroundOwn#gotHit(Position)} will be called in this turn later on
     */
    public void setShip(Position position, Ship.LifeStatus lifeStatus, boolean hitLater) {
        numHitShipsFields++;
        Ship newShip = new Ship(position);
        boolean concatenated = false;
        int[][] adjacentPositions = {{-1, 0}, {0, -1}, {1, 0}, {0, 1}};
        for (int[] adjacentPos : adjacentPositions) {
            int x = position.getX() + adjacentPos[0];
            int y = position.getY() + adjacentPos[1];
            if (x >= 0 && y >= 0) {
                Position p = new Position(x, y);
                if (!p.isOutsideOfPlayground(this.size) && this.elements[p.getY()][p.getX()].type == FieldType.SHIP) {
                    Ship adjacentShip = (Ship) this.elements[p.getY()][p.getX()].element;
                    adjacentShip.concatenateShips(newShip);
                    this.elements[p.getY()][p.getX()].element = newShip;
                    concatenated = true;
                    newShip = adjacentShip;
                }
            }
        }
        if (!concatenated) {
            putShip(newShip);
        }
        if (hitLater) {
            newShip.setLives(lifeStatus == Ship.LifeStatus.ALIVE ? 2 : 1); // Lives will be taken in gotHit later
        } else {
            newShip.setLives(lifeStatus == Ship.LifeStatus.ALIVE ? 1 : 0);
        }
        this.elements[position.getY()][position.getX()] = new Field(FieldType.SHIP, newShip, true);
        if (lifeStatus == Ship.LifeStatus.SUNKEN) {
            for (Position p : getSurroundingWaterPositions(newShip)) {
                setWater(p);
            }
        }
    }
}
