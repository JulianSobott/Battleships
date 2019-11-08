package core;

import core.communication_data.*;
import core.utils.Logger;

import java.util.HashMap;

public class PlaygroundOwn extends Playground {

    private HashMap<ShipID, Ship> shipHashMap = new HashMap<>();

    public PlaygroundOwn(int size) {
        super(size);
        this.resetFields(FieldType.WATER);
    }

    /**
     * Place a ship on the playground if it's possible.
     * @param position Position where the ship is intended to be placed.
     * @return An result that indicates whether it was successfully placed or not.
     */
    public PlaceShipResult placeShip(ShipPosition position){
        Ship ship = this.shipPool.getShip(position.getLENGTH());
        PlaceShipResult res = this.placeShip(position, ship);
        if(!res.isSuccessfullyPlaced()){
            this.shipPool.releaseShip(ship);
        }
        return res;
    }

    /**
     * Place an already created ship on the playground if it's possible.
     * @param position Position where the ship is intended to be placed.
     * @return An result that indicates whether it was successfully placed or not.
     */
    private PlaceShipResult placeShip(ShipPosition position, Ship ship){
        if(ship == null)
            return PlaceShipResult.failed(position, null, PlaceShipResult.Error.NO_MORE_SHIPS);
        if(position.isOutsideOfPlayground(this.size))
            return PlaceShipResult.failed(position, null, PlaceShipResult.Error.NOT_ON_PLAYGROUND);
        if(!this.canPlaceShip(position)){
            return PlaceShipResult.failed(position, null, PlaceShipResult.Error.SPACE_TAKEN);
        }else{
            for(Position p : position.generateIndices()){
                Field f = new Field(FieldType.SHIP, ship);
                this.elements[p.getY()][p.getX()] = f;
            }
            ShipID shipID = ship.getId();
            ship.setShipPosition(position);
            this.shipHashMap.put(shipID, ship);
            return PlaceShipResult.success(position, shipID);
        }
    }

    /**
     * Move a ship to new position if it can be placed there.
     *
     * @param id Id of the ship that shall be moved
     * @param newPosition New position of the ship
     * @return PlaceShipResult of the placeShip method.
     */
    public PlaceShipResult moveShip(ShipID id, ShipPosition newPosition){
        Ship ship = this.getShipByID(id);
        if(ship == null) {
            return PlaceShipResult.failed(newPosition, id, PlaceShipResult.Error.ID_NOT_EXIST);
        }
        ShipPosition oldPosition = ship.getShipPosition();
        this.resetFields(FieldType.WATER, oldPosition.generateIndices());
        PlaceShipResult res = this.placeShip(newPosition, ship);
        if(!res.isSuccessfullyPlaced()){
            this.placeShip(oldPosition, ship);
        }
        return res;
    }

    /**
     * Delete a ship from the playground and replace all fields with water fields.
     * @param id ID of the ship
     * @return true if the id exists, false otherwise
     */
    public boolean deleteShip(ShipID id){
        Ship ship = this.getShipByID(id);
        if(ship == null) {
            return false;
        }
        else {
            this.resetFields(FieldType.WATER, ship.getShipPosition().generateIndices());
            this.shipPool.releaseShip(ship);
            this.shipHashMap.remove(id);
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
            if(position.isOutsideOfPlayground(this.size))
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

    /**
     *
     * @param shipID Searched shipID
     * @return An Ship object if the ID exists, null otherwise
     */
    private Ship getShipByID(ShipID shipID){
        return this.shipHashMap.get(shipID);
    }

    public void printField(){
        StringBuilder s = new StringBuilder();
        for(Field[] row : this.elements){
            for(Field f : row){
                if(f == null){
                    s.append("N");
                }
                else if(f.type == FieldType.SHIP){
                    s.append("S");
                }
                else if(f.type == FieldType.WATER){
                    s.append("~");
                }else if(f.type == FieldType.FOG){
                    s.append("=");
                }
            }
            s.append("\n");
        }
        Logger.debug(s);
    }
}
