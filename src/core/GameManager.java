package core;

import core.communication_data.*;

public class GameManager implements GameManagerInterface {

    private Player player1, player2;
    private Player currentPlayer;


    @Override
    public boolean connectToServer(String ip, int port) {
        return false;
    }

    @Override
    public GameSettings getGameSettings() {
        return null;
    }

    @Override
    public void newGame(GameSettings settings) {

    }

    @Override
    public PlaceShipResult placeShip(ShipPosition pos) {
        return null;
    }

    @Override
    public PlaceShipResult moveShip(int id, ShipPosition pos) {
        return null;
    }

    @Override
    public boolean deleteShip(int id) {
        return false;
    }

    @Override
    public TurnResult ownTurn(Position position) {
        return null;
    }

    @Override
    public TurnResult enemyTurn() {
        return null;
    }
}
