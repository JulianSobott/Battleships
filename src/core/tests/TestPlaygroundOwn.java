package core.tests;

import core.PlaygroundOwn;
import core.communication_data.PlaceShipResult;
import core.communication_data.ShipID;
import core.communication_data.ShipPosition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestPlaygroundOwn{

    private final PlaygroundOwn playground = new PlaygroundOwn(6);

    @BeforeEach
    void cleanPlayground(){
        playground.resetFieldsToWater();
    }

    @Test
    void canPlaceShip(){
        ShipPosition p1 = new ShipPosition(0, 0, ShipPosition.Direction.HORIZONTAL, 6);
        assert playground.canPlaceShip(p1): "Ship should be possible to place";

        ShipPosition p2 = new ShipPosition(5, 5, ShipPosition.Direction.HORIZONTAL, 2);
        assert !playground.canPlaceShip(p2): "Ship shouldn't be possible to place";

        playground.placeShip(p1);
        ShipPosition p3 = new ShipPosition(1, 1, ShipPosition.Direction.HORIZONTAL, 1);
        assert !playground.canPlaceShip(p3): "Ship shouldn't be possible to place";

        ShipPosition p4 = new ShipPosition(0, 2, ShipPosition.Direction.VERTICAL, 2);
        assert playground.canPlaceShip(p4): "Ship should be possible to place";

        try{
            ShipPosition p5 = new ShipPosition(6, 6, ShipPosition.Direction.HORIZONTAL, 1);
            assert false: "Expected AssertionError";
        }catch (AssertionError ignored){}
    }

    @Test
    void placeShip(){
        ShipPosition p1 = new ShipPosition(0, 0, ShipPosition.Direction.HORIZONTAL, 6);
        PlaceShipResult res = playground.placeShip(p1);
        assert res.isSuccessfullyPlaced(): "Ship should be placed";

        ShipPosition p2 = new ShipPosition(0, 2, ShipPosition.Direction.HORIZONTAL, 7);
        res = playground.placeShip(p2);
        assert !res.isSuccessfullyPlaced(): "Ship shouldn't be placed";

        ShipPosition p3 = new ShipPosition(0, 2, ShipPosition.Direction.VERTICAL, 2);
        res = playground.placeShip(p3);
        assert res.isSuccessfullyPlaced(): "Ship should be placed";

        ShipPosition p4 = new ShipPosition(1, 2, ShipPosition.Direction.VERTICAL, 2);
        res = playground.placeShip(p3);
        assert !res.isSuccessfullyPlaced(): "Ship shouldn't be placed";
    }

    @Test
    void deleteShip(){
        ShipPosition p1 = new ShipPosition(0, 0, ShipPosition.Direction.HORIZONTAL, 6);
        PlaceShipResult res = playground.placeShip(p1);
        boolean deleted = playground.deleteShip(res.getShipID());
        assert deleted: "Ship should be deleted";

        deleted = playground.deleteShip(new ShipID(100));
        assert !deleted: "Ship shouldn't be deleted";
    }

    @Test
    void moveShip(){
        ShipPosition p1 = new ShipPosition(0, 0, ShipPosition.Direction.HORIZONTAL, 2);
        PlaceShipResult res1 = playground.placeShip(p1);

        ShipPosition p2 = new ShipPosition(1, 0, ShipPosition.Direction.HORIZONTAL, 2);
        PlaceShipResult res2 = playground.moveShip(res1.getShipID(), p2);
        assert res2.isSuccessfullyPlaced(): "Ship should be moved";
        assert res2.getPosition() == p2 : "Ship is not moved correctly";

        ShipPosition p3 = new ShipPosition(4, 4, ShipPosition.Direction.HORIZONTAL, 2);
        PlaceShipResult res3 = playground.placeShip(p3);
        ShipPosition p4 = new ShipPosition(0, 1, ShipPosition.Direction.HORIZONTAL, 2);
        PlaceShipResult res4 = playground.moveShip(res3.getShipID(), p4);
        assert !res4.isSuccessfullyPlaced(): "Ship should not be moved";
        //assert res4.getPosition() == p3 : "Ship position is not restored correctly";
        //TODO: what position should be stored in the PlaceShipResult on failure
    }
}
