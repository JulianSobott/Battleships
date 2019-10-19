package core;

import core.communication_data.*;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * Move a ship to new position if it can be placed there.
     * Algorithm: copy ship -> deleted ship -> place ship -> restore copy if place fails
     * @param id Id of the ship that shall be moved
     * @param newPosition New position of the ship
     * @return PlaceShipResult of the placeShip method.
     */
    public PlaceShipResult moveShip(ShipID id, ShipPosition newPosition){
        // TODO: if ShipPosition would be stored in ship it could be easier restored
        Ship shipCopy = this.getShipByID(id);
        Position[] oldPositions = this.getPositionsByShipID(id);
        this.deleteShip(id);
        PlaceShipResult res = this.placeShip(newPosition);
        if(!res.isSuccessfullyPlaced()){
            for (Position p: oldPositions) {
                this.elements[p.getY()][p.getX()] = new Field(FieldType.SHIP, shipCopy);
            }
        }
        return res;
    }

    /**
     * Fill all fields that are null with a water field
     */
    private void fillWithWater() {
        for (int y = 0; y < this.size; y++) {
            for (int x = 0; x < this.size; x++) {
                Field field = this.elements[y][x];
                if(field == null){
                    this.elements[y][x] = new Field(FieldType.WATER, new WaterElement());
                }
            }
        }
    }

    /**
     * Delete a ship from the playground and replace all fields with water fields.
     * @param id ID of the ship
     * @return true if the id exists, false otherwise
     */
    public boolean deleteShip(ShipID id){
        Position[] positions = this.getPositionsByShipID(id);
        if(positions.length == 0)
            return false;
        else {
            for(Position p : positions){
                this.elements[p.getY()][p.getX()] = new Field(FieldType.WATER, new WaterElement());
            }
            return true;
        }
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

    /**
     * Reset all fields to water fields.
     * All references to ships are lost.
     */
    public void resetFieldsToWater(){
        super.resetFields(FieldType.WATER);
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

    private Position[] getPositionsByShipID(ShipID id){
        List<Position> positions = new ArrayList<>();
        for (int y = 0; y < this.size; y++) {
            for (int x = 0; x < this.size; x++) {
                Field field = this.elements[y][x];
                if(field != null && field.type == FieldType.SHIP && ((Ship)field.element).getId().equals(id)){
                    positions.add(new Position(x, y));
                }
            }
        }
        Position[] positionsArr = new Position[positions.size()];
        return positions.toArray(positionsArr);
    }

    private Ship getShipByID(ShipID shipID){
        Position[] positions = this.getPositionsByShipID(shipID);
        if(positions.length == 0)
            return null;
        int x = positions[0].getX();
        int y = positions[0].getY();
        return (Ship)this.elements[y][x].element;
    }
}
