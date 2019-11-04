package core;

import core.communication_data.ShipList;
import core.communication_data.ShipPosition;

import java.util.HashMap;
import java.util.HashSet;

/**
 *
 *
 */
public class ShipPool {

    static class ObjectPool<T> {

        HashSet<T> available = new HashSet<>();
        HashSet<T> inUse = new HashSet<>();

        T getObject(){
            if(available.isEmpty()){
                return null;
            }
            T o =  available.iterator().next();
            available.remove(o);
            inUse.add(o);
            return o;
        }

        void releaseObject(T object) {
            inUse.remove(object);
            available.add(object);
        }

    }

    private HashMap<Integer, ObjectPool<Ship>> pools = new HashMap<>();

    public ShipPool(ShipList shipList){
        for(ShipList.Pair pair : shipList){
            ObjectPool<Ship> pool = new ObjectPool<Ship>(){
                ObjectPool<Ship> init(int num, int length){
                    for (int i = 0; i < num; i++) {
                        // TODO: SET ID of ship
                        Ship s = new Ship(length, 1,
                                new ShipPosition(0, 0, ShipPosition.Direction.HORIZONTAL, length));
                        this.available.add(s);
                    }
                    return this;
                }
            }.init(pair.getNum(), pair.getSize());
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
        return this.pools.get(shipSize).getObject();
    }

    /**
     * Mark a ship as not used anymore. Call when a ship is not present at the board
     *
     * @param ship Ship object that is no free to use
     */
    public void releaseShip(Ship ship){
        int size = ship.getShipPosition().getLENGTH();
        this.pools.get(size).releaseObject(ship);
    }

    private void cleanUp(Ship ship){
        // TODO: reset ship position and lives
    }


}
