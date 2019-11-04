package core.tests;

import core.Ship;
import core.ShipPool;
import core.communication_data.ShipList;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

class TestShipPool {


    @Test
    void testGet(){
        ShipList s = new ShipList(new HashMap<>(){{
            put(1, 1);
            put(2, 1);
            put(3, 1);
        }});
        ShipPool shipPool = new ShipPool(s);
        Ship s1 = shipPool.getShip(1);
        assert s1 != null;
        Ship s2 = shipPool.getShip(1);
        assert s2 == null;
        shipPool.releaseShip(s1);
        s2 = shipPool.getShip(1);
        assert s2.equals(s1);
    }
}
