package core.tests;

import core.Ship;
import core.ShipPool;
import core.communication_data.ShipList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

class TestShipPool {


    private ShipPool shipPool;

    @BeforeEach
    void setUp(){
        ShipList s = new ShipList(new HashMap<>(){{
            put(1, 1);
            put(2, 1);
            put(3, 1);
        }});
        this.shipPool = new ShipPool(s);
    }

    @Test
    void testGet(){
        Ship s1 = shipPool.getShip(1);
        assert s1 != null;
        Ship s2 = shipPool.getShip(1);
        assert s2 == null;
        shipPool.releaseShip(s1);
        s2 = shipPool.getShip(1);
        assert s2 == s1;
    }

    @Test
    void testRelease(){
        Ship s1 = shipPool.getShip(1);
        shipPool.releaseShip(s1);
        shipPool.releaseShip(s1);

        Ship s2 = shipPool.getShip(1);
        assert s1 == s2;
        Ship s3 = shipPool.getShip(1);
        assert s3 == null;
    }
}
