package core;

import core.communication_data.ShipList;
import core.communication_data.ShipPosition;
import core.utils.ObjectPool;

import java.util.HashMap;

/**
 * Creates all ships objects at once. You can get and release objects.
 *
 */
public class ShipPool {

    static class ShipObjectPool extends ObjectPool<Ship> {
        ShipObjectPool(int length, int num){
            for (int i = 0; i < num; i++) {
                Ship s = new Ship(length);
                this.available.add(s);
            }
        }
    }

    private HashMap<Integer, ObjectPool<Ship>> pools = new HashMap<>();

    public ShipPool(ShipList shipList){
        for(ShipList.Pair pair : shipList){
            ObjectPool<Ship> pool = new ShipObjectPool(pair.getSize(), pair.getNum());
            this.pools.put(pair.getSize(), pool);
        }
    }

    /**
     * Get a ship object with given size, that is not already used.
     *
     * @param shipSize Size of a ship
     * @return An ship object if an object is available, null if all objects are in use.
     */
    public Ship getShip(int shipSize){
        ObjectPool<Ship> pool = this.pools.get(shipSize);
        if(pool == null){
            return null;
        }
        return pool.getObject();
    }

    /**
     * Mark a ship as not used anymore. Call when a ship is not present at the board
     *
     * @param ship Ship object that is no free to use
     */
    public void releaseShip(Ship ship){
        int size = ship.getShipPosition().getLENGTH();
        assert this.pools.containsKey(size);
        this.pools.get(size).releaseObject(ship);
    }

    private void cleanUp(Ship ship){
        // TODO: reset ship position and lives
    }

    public void releaseAll() {
        for(ObjectPool<Ship> pool : this.pools.values()){
            pool.releaseAll();
        }
    }


}
