package core.tests;

import core.Ship;
import core.communication_data.Position;
import core.communication_data.ShipPosition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestShip {

    @Test
    void testConcatenateShips1() {
        Ship s1 = new Ship(new Position(0, 0));
        Ship s2 = new Ship(new Position(0, 1));
        s1.concatenateShips(s2);
        ShipPosition expectedPosition = new ShipPosition(0, 0, ShipPosition.Direction.VERTICAL, 2);
        assertEquals(expectedPosition, s1.getShipPosition());
    }

    @Test
    void testConcatenateShips2() {
        Ship s1 = new Ship(new Position(0, 0));
        Ship s2 = new Ship(new Position(1, 0));
        s1.concatenateShips(s2);
        ShipPosition expectedPosition = new ShipPosition(0, 0, ShipPosition.Direction.HORIZONTAL, 2);
        assertEquals(expectedPosition, s1.getShipPosition());
    }

    @Test
    void testConcatenateShips3() {
        Ship s1 = new Ship(new Position(0, 0));
        Ship s2 = new Ship(new Position(1, 0));
        Ship s3 = new Ship(new Position(2, 0));
        s1.concatenateShips(s2);
        s1.concatenateShips(s3);
        ShipPosition expectedPosition = new ShipPosition(0, 0, ShipPosition.Direction.HORIZONTAL, 3);
        assertEquals(expectedPosition, s1.getShipPosition());
    }
}
