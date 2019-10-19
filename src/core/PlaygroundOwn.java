package core;

import core.communication_data.*;

public class PlaygroundOwn extends Playground {

    public PlaygroundOwn(int size) {
        super(size);
    }

    /**
     * Place a ship on the playground if it's possible.
     * @param position Position where the ship is intended to be placed.
     * @return An result that indicates whether it was successfully placed or not.
     */
    public PlaceShipResult placeShip(ShipPosition position){
        assert  position.getX() >= 0 && position.getX() < this.size &&
                position.getX() >= 0 && position.getX() < this.size: "Ship position not on the playground";
        boolean successfullyPlaced = false;
        ShipID shipID = null;
        if(this.canPlaceShip(position)){
            Ship ship = new Ship(position.getLENGTH());
            for(Position p : position.generateIndices()){
                Field f = new Field(FieldType.SHIP, ship);
                this.elements[p.getY()][p.getX()] = f;
            }
            successfullyPlaced = true;
            shipID = ship.getId();
        }
        return new PlaceShipResult(successfullyPlaced, position, shipID);
    }

    public PlaceShipResult moveShip(int id, ShipPosition position){
        return null;
    }

    public boolean deleteShip(int id){
        return false;
    }

    /**
     * Checks if the ship can be placed, without overlapping another ship or being to close to another.
     * Between two ships must be at least one water field.
     * Ships are allowed to be placed at the edge of the playground.
     * @param position Position where the ship is intended to be placed.
     * @return true if ship can be placed, false otherwise.
     */
    public boolean canPlaceShip(ShipPosition position){
        int[][] surroundingFields = {{-1, -1}, {-1, 0}, {0, -1}, {0, 0}, {0, 1}, {1, 0}, {1, 1}, {-1, 1}, {1, -1}};
        for(Position p : position.generateIndices()){
            // position not on board
            if(p.getX() >= this.size || p.getX() < 0 || p.getY() >= this.size || p.getY() < 0)
                return false;
            // surrounding field is ship
            for(int[] surroundingField : surroundingFields){
                // 0 <= x < size
                int x = Math.min(Math.max(p.getX() + surroundingField[0], 0), this.size - 1);
                // 0 <= y < size
                int y = Math.min(Math.max(p.getY() + surroundingField[1], 0), this.size - 1);

                if(this.elements[y][x] != null && this.elements[y][x].type == FieldType.SHIP)
                    return false;
            }
        }
        return true;
    }

    public void resetFieldsToWater(){

    }

    public void placeShipsRandom(ShipList shipList){

    }

    /**
     *
     * @param position x, y coordinates of the shot.
     * @return
     */
    public ShotResult gotHit(Position position){
        Field f = this.elements[position.getY()][position.getX()];
        f.hit = true;
        f.element.gotHit();
        if(f.type == FieldType.SHIP){
            return new ShotResultShip(position, FieldType.SHIP, ((Ship)f.element).getStatus());
        }else{
            return new ShotResultWater(position, f.type);
        }
    }
}
