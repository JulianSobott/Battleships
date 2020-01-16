package core.playgrounds;

import core.Ship;
import core.ShipPool;
import core.communication_data.*;
import core.utils.Random;
import core.utils.logging.LoggerLogic;

public class PlaygroundPlaceable extends Playground {

    private boolean logDeactivatedShipPlacement = false;
    private ShipPool shipPool;


    public PlaygroundPlaceable() { // Jackson deserialization
    }

    public PlaygroundPlaceable(int size) {
        super(size);
        this.resetFields(Playground.FieldType.WATER);
        this.shipPool = new ShipPool(ShipList.fromSize(size));
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
                Playground.Field f = new Playground.Field(Playground.FieldType.SHIP, ship);
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
            this.resetFields(Playground.FieldType.WATER, oldPosition.generateIndices());
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
            this.resetFields(Playground.FieldType.WATER, ship.getShipPosition().generateIndices());
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

                if(this.elements[y][x] != null && this.elements[y][x].type == Playground.FieldType.SHIP)
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
        boolean foundPlace = true;
        for (int iteration = 0; iteration < max_tries; iteration++) {
            this.resetAll(Playground.FieldType.WATER);
            foundPlace = true;
            for(ShipList.Pair pair : shipList){
                for(int i = 0; i < pair.getNum() && foundPlace; i++){
                    foundPlace = this.placeSingleShipRandom(pair.getSize());
                }
                if(!foundPlace) break;
            }
            if(foundPlace) {
                LoggerLogic.debug("Found in iteration: " + iteration);
                printField();
                LoggerLogic.debug("PlaygroundOwn hash=" + this.hashCode());
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

    private boolean placeSingleShipRandom(int length){
        int max_iterations = 100;
        for(int i = 0; i < max_iterations; i++){
            int x = Random.random.nextInt(this.size);
            int y = Random.random.nextInt(this.size);
            ShipPosition.Direction dir = Random.random.nextBoolean() ? ShipPosition.Direction.HORIZONTAL :
                    ShipPosition.Direction.VERTICAL;
            ShipPosition pos = new ShipPosition(x, y, dir, length);
            if(this.placeShip(pos).isSuccessfullyPlaced())
                return true;
        }
        return false;
    }

    public boolean areAllShipsPlaced() {
        return this.shipPool.areAllShipsPlaced();
    }

    @Override
    public void resetAll(FieldType type) {
        super.resetAll(type);
        this.shipPool.releaseAll();
    }

}
