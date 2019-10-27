package core;

import core.communication_data.*;

public interface GameManagerInterface {
    // Server
    public boolean connectToServer(String ip, int port);
    public GameSettings getGameSettings();

    // create game
    public void newGame(GameSettings settings);

    // Ship Placement

    /**
     *  Request for positioning a ship.
     * Checks whether the position is valid and free are made in the implementation.
     *
     * @param pos Wanted position where ship should be placed
     * @return A result with information if it was placed
     */
    PlaceShipResult placeShip(ShipPosition pos);

    /**
     * Request for a move of a ship to a new position.
     * Checks whether the new position is valid and free are made in the implementation.
     * If it is not free the ship stays at the old position.
     *
     * @param id Id of the ship. The id is part of the PlaceShipResult.
     * @param pos The wanted new position
     * @return A result with information, if the move was successfully
     */
    PlaceShipResult moveShip(ShipID id, ShipPosition pos);

    /**
     * Requests a deletion of a ship from the playground.
     *
     * @param id Id of the ship. The id is part of the PlaceShipResult.
     * @return true if the deletion was successful, false otherwise.
     */
    boolean deleteShip(ShipID id);

    // turn
    public TurnResult ownTurn(Position position);
    public TurnResult enemyTurn();
}
