package core;

import core.communication_data.*;

public interface GameManagerInterface {
    // Server
    public boolean connectToServer(String ip, int port);
    public GameSettings getGameSettings();

    // Ship Placement
    public PlaceShipResult placeShip(ShipPosition pos);
    public PlaceShipResult moveShip(int id, ShipPosition pos);
    public boolean deleteShip(int id);

    // turn
    public TurnResult ownTurn(Position position);
    public TurnResult enemyTurn();
}
