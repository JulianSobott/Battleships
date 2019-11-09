package core.tests;

import core.communication_data.Position;
import core.communication_data.ShipPosition;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class TestCommunicationData {

    @Test
    void testShipPosition(){
        boolean thrown = false;
        try{
            ShipPosition p1 = new ShipPosition(-1, -1, ShipPosition.Direction.HORIZONTAL, 1);
        }catch (AssertionError ignored){
            thrown = true;
        }
        assert thrown : "Expected AssertionError! No negative positions are allowed";
        thrown = false;
        try{
            ShipPosition p2 = new ShipPosition(1, 1, ShipPosition.Direction.HORIZONTAL, -1);
        }catch (AssertionError ignored){thrown = true;}
        assert thrown: "Expected AssertionError! No negative length is allowed";
        ShipPosition p3 = new ShipPosition(2, 1, ShipPosition.Direction.HORIZONTAL, 2);
        assert p3.getX() == 2 && p3.getY() == 1 && p3.getDirection() == ShipPosition.Direction.HORIZONTAL
                && p3.getLength() == 2: "Attributes not properly set";
        Position[] positionsExpected = {new Position(2, 1), new Position(3, 1)};
        assert Arrays.equals(positionsExpected, p3.generateIndices());
    }
}
