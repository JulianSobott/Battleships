package core;

import core.communication_data.*;
import core.utils.logging.LoggerLogic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class PlaygroundOwn extends Playground {

    private HashMap<ShipID, Ship> shipHashMap = new HashMap<>();
    private boolean logDeactivatedShipPlacement = false;

    public PlaygroundOwn() { // Jackson deserialization
    }

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
        Ship ship = this.shipPool.getShip(position.getLength());
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
        if (!this.logDeactivatedShipPlacement)
            LoggerLogic.info("placeShip: position=" + position + ", ship=" + ship);
        PlaceShipResult res;
        if(ship == null)
            res = PlaceShipResult.failed(position, null, PlaceShipResult.Error.NO_MORE_SHIPS);
        else if(position.isOutsideOfPlayground(this.size))
            res = PlaceShipResult.failed(position, null, PlaceShipResult.Error.NOT_ON_PLAYGROUND);
        else if(!this.canPlaceShip(position)){
            res =  PlaceShipResult.failed(position, null, PlaceShipResult.Error.SPACE_TAKEN);
        }else{
            for(Position p : position.generateIndices()){
                Field f = new Field(FieldType.SHIP, ship);
                this.elements[p.getY()][p.getX()] = f;
            }
            ShipID shipID = ship.getId();
            ship.setShipPosition(position);
            this.shipHashMap.put(shipID, ship);
            res =  PlaceShipResult.success(position, shipID);
        }
        if (!this.logDeactivatedShipPlacement)
            LoggerLogic.info( "placeShip return: PlaceShipResult=" + res);
        return res;
    }

    /**
     * Move a ship to new position if it can be placed there.
     *
     * @param id Id of the ship that shall be moved
     * @param newPosition New position of the ship
     * @return PlaceShipResult of the placeShip method.
     */
    public PlaceShipResult moveShip(ShipID id, ShipPosition newPosition){
        LoggerLogic.info( "moveShip: id=" + id + "newPosition=" + newPosition );
        Ship ship = this.getShipByID(id);
        PlaceShipResult res;
        if(ship == null) {
            res = PlaceShipResult.failed(newPosition, id, PlaceShipResult.Error.ID_NOT_EXIST);
        }else{
            ShipPosition oldPosition = ship.getShipPosition();
            this.resetFields(FieldType.WATER, oldPosition.generateIndices());
            res = this.placeShip(newPosition, ship);
            if(!res.isSuccessfullyPlaced()){
                this.placeShip(oldPosition, ship);
            }
        }
        LoggerLogic.info( "moveShip return: PlaceShipResult=" + res);
        return res;
    }

    /**
     * Delete a ship from the playground and replace all fields with water fields.
     * @param id ID of the ship
     * @return true if the id exists, false otherwise
     */
    public boolean deleteShip(ShipID id){
        LoggerLogic.info( "deleteShip: id=" + id);
        Ship ship = this.getShipByID(id);
        boolean res;
        if(ship == null) {
            res = false;
        }
        else {
            this.resetFields(FieldType.WATER, ship.getShipPosition().generateIndices());
            this.shipPool.releaseShip(ship);
            this.shipHashMap.remove(id);
            res = true;
        }
        LoggerLogic.info( "deleteShip return: PlaceShipResult=" + res);
        return res;
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
        // position not on board
        if(position.isOutsideOfPlayground(this.size))
            return false;
        for(Position p : position.generateIndices()){
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

    public PlaceShipsRandomRes placeShipsRandom() {
        ShipList shipList = ShipList.fromSize(this.size);
        return this.placeShipsRandom(shipList);
    }

    public PlaceShipsRandomRes placeShipsRandom(ShipList shipList) {
        this.logDeactivatedShipPlacement = true;
        int max_tries = 10000;
        Random random = new Random(1000);
        boolean foundPlace = true;
        for (int iteration = 0; iteration < max_tries; iteration++) {
            this.resetAll(FieldType.WATER);
            foundPlace = true;
            for(ShipList.Pair pair : shipList){
                for(int i = 0; i < pair.getNum() && foundPlace; i++){
                    foundPlace = this.placeSingleShipRandom(pair.getSize(), random);
                }
                if(!foundPlace) break;
            }
            if(foundPlace) {
                LoggerLogic.debug("Found in iteration: " + iteration);
                break;
            }
        }
        this.logDeactivatedShipPlacement = false;
        if(foundPlace){
            LoggerLogic.info("Successfully placed ships");
            PlaceShipsRandomRes.ShipData[] data = new PlaceShipsRandomRes.ShipData[shipList.getTotalNumberOfShips()];
            int i = 0;
            for(Ship s : this.shipHashMap.values()){
                data[i++] = new PlaceShipsRandomRes.ShipData(new ShipPosition(s.getShipPosition()), s.getId());
            }
            return PlaceShipsRandomRes.success(data);
        }else{
            LoggerLogic.warning("Could not place ships");
            return PlaceShipsRandomRes.failure();
        }

    }

    public boolean areAllShipsPlaced() {
        return this.shipPool.areAllShipsPlaced();
    }

    private boolean placeSingleShipRandom(int length, Random rand){
        int max_iterations = 100;
        for(int i = 0; i < max_iterations; i++){
            int x = rand.nextInt(this.size);
            int y = rand.nextInt(this.size);
            ShipPosition.Direction dir = rand.nextBoolean() ? ShipPosition.Direction.HORIZONTAL :
                    ShipPosition.Direction.VERTICAL;
            ShipPosition pos = new ShipPosition(x, y, dir, length);
            if(this.placeShip(pos).isSuccessfullyPlaced())
                return true;
        }
        return false;
    }

    /**
     *
     * @param position x, y coordinates of the shot.
     * @return A ShotResult, with information about what field was hit
     */
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
    private Position[] getSurroundingWaterPositions(Ship s) {
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

    /**
     *
     * @param shipID Searched shipID
     * @return An Ship object if the ID exists, null otherwise
     */
    private Ship getShipByID(ShipID shipID){
        return this.shipHashMap.get(shipID);
    }
}
