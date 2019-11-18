package core.tests;

import core.communication_data.ShipList;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestShipList {

    @Test
    void fromSize() {
        ShipList list = ShipList.fromSize(5);
        HashMap<Integer, Integer> ships = list.getShips();
        HashMap<Integer, Integer> expectedShips = new HashMap<>() {{
            put(2, 1);
            put(3, 1);
            put(4, 0);
            put(5, 0);
        }};
        assertEquals(expectedShips, ships);
    }
}
