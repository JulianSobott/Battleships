package player;

import core.Player;
import core.PlaygroundOwnPlaceable;
import core.PlaygroundPlaceable;
import core.communication_data.PlaceShipResult;
import core.communication_data.PlaceShipsRandomRes;
import core.communication_data.ShipID;
import core.communication_data.ShipPosition;

public abstract class PlayerPlacer extends Player {

    public PlayerPlacer(int index, String name, int playgroundSize) {
        super(index, name, playgroundSize);
        this.playgroundOwn = new PlaygroundOwnPlaceable(playgroundSize);
    }

    public PlaceShipResult placeShip(ShipPosition position) {
        PlaceShipResult res = getPlaygroundOwnPlaceable().placeShip(position);
        getPlaygroundOwnPlaceable().printField();
        return res;
    }

    public PlaceShipResult moveShip(ShipID id, ShipPosition position) {
        return getPlaygroundOwnPlaceable().moveShip(id, position);
    }

    public boolean deleteShip(ShipID id) {
        boolean res = getPlaygroundOwnPlaceable().deleteShip(id);
        getPlaygroundOwnPlaceable().printField();
        return res;
    }

    public PlaceShipsRandomRes placeShipsRandom() {
        return getPlaygroundOwnPlaceable().placeShipsRandom();
    }

    private PlaygroundPlaceable getPlaygroundOwnPlaceable() {
        return (PlaygroundOwnPlaceable)this.playgroundOwn;
    }
}
