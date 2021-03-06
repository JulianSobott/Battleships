package core.tests;

import core.playgrounds.PlaygroundPlaceable;
import core.Ship;
import core.communication_data.PlaceShipResult;
import core.communication_data.ShipID;
import core.communication_data.ShipList;
import core.communication_data.ShipPosition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestPlaygroundPlaceable {

    // TODO: Mock ShipList to pass tests
    private PlaygroundPlaceable playground;
    private final int SIZE = 6;

    @BeforeEach
    void cleanPlayground(){
        playground = new PlaygroundPlaceable(SIZE);
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
    }

    @Test
    void placeShip(){
        ShipPosition p1 = new ShipPosition(0, 0, ShipPosition.Direction.HORIZONTAL, 6);
        PlaceShipResult res = playground.placeShip(p1);
        assert res.isSuccessfullyPlaced(): "Ship should be placed";

        ShipPosition p2 = new ShipPosition(4, 2, ShipPosition.Direction.HORIZONTAL, 3);
        res = playground.placeShip(p2);
        assert !res.isSuccessfullyPlaced(): "Ship shouldn't be placed";
        assert res.getERROR() == PlaceShipResult.Error.NOT_ON_PLAYGROUND: "Error not correctly set";

        ShipPosition p3 = new ShipPosition(0, 2, ShipPosition.Direction.VERTICAL, 2);
        res = playground.placeShip(p3);
        assert res.isSuccessfullyPlaced(): "Ship should be placed";

        ShipPosition p4 = new ShipPosition(1, 2, ShipPosition.Direction.VERTICAL, 2);
        res = playground.placeShip(p3);
        assert !res.isSuccessfullyPlaced(): "Ship shouldn't be placed";
        assert res.getERROR() == PlaceShipResult.Error.SPACE_TAKEN: "Error not correctly set";
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
        assert res4.getPosition() == p4 : "Ship position is not set correctly";
    }

    @Test
    void moveShipWrongID() {
        // Move a ship which ID does not exist
        ShipID id = ShipID.getNextShipID();
        ShipPosition pos = new ShipPosition(0, 0, ShipPosition.Direction.HORIZONTAL, 2);
        PlaceShipResult res = playground.moveShip(id, pos);
        assert !res.isSuccessfullyPlaced();
        assert res.getERROR() == PlaceShipResult.Error.ID_NOT_EXIST;
    }

    @Test
    void placeShipsRandom(){
        ShipList list = ShipList.fromSize(SIZE);
        playground.placeShipsRandom(list);
    }

    @Test
    void testGetAllShips() {
        ShipPosition p1 = new ShipPosition(0, 0, ShipPosition.Direction.HORIZONTAL, 2);
        ShipPosition p2 = new ShipPosition(0, 2, ShipPosition.Direction.HORIZONTAL, 3);
        playground.placeShip(p1);
        playground.placeShip(p2);
        Ship[] ships = playground.getAllShips();
        assertEquals(ships.length, 2);
        assert ships[0].getShipPosition().equals(p1) || ships[0].getShipPosition().equals(p2);
        assert ships[1].getShipPosition().equals(p1) || ships[1].getShipPosition().equals(p2);
    }
}
