package core;

import core.communication_data.*;

public interface GameManagerInterface {
    // Server
    public boolean connectToServer(String ip, int port);

    public GameSettings getGameSettings();

    // create game
    public NewGameResult newGame(GameSettings settings);

    // Ship Placement

    /**
     * Request for positioning a ship.
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
     * @param id  Id of the ship. The id is part of the PlaceShipResult.
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

    /**
     * Request to start the shooting.
     * Only possible, when all ships are placed.
     *
     * @return whether it is possible to start or not.
     */
    StartShootingRes startShooting();

    /**
     * player shoots at a position on the enemies board.
     *
     * @param position position of the shot destination.
     * @return ShotResult
     */
    TurnResult shoot(Position position);

    /**
     * Call every time the enemy will make a shot.
     * Returns the last enemy TurnResult.
     * If it is not present it waits until it is present.
     * Necessary to update fields in a gui.
     *
     * @return the last TurnResult of the enemy.
     */
    TurnResult getEnemyTurn();

}
