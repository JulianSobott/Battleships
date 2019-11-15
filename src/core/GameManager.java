package core;

import core.communication_data.*;
import player.PlayerAI;
import player.PlayerHuman;

public class GameManager implements GameManagerInterface {

    /**
     * Normally local player which controls the GUI.
     * Except at AI vs AI.
     */
    private Player player1;

    /**
     * Normally AI or network player.
     * Except local vs local.
     * <p>
     * At the remote side when played over network the players would be swapped.
     */
    private Player player2;

    private Player currentPlayer;

    private TurnResult lastTurn;


    @Override
    public boolean connectToServer(String ip, int port) {
        return false;
    }

    @Override
    public GameSettings getGameSettings() {
        return null;
    }

    @Override
    public NewGameResult newGame(GameSettings settings) {
        this.player1 = settings.getP1();
        this.player2 = settings.getP2();
        assert this.player1 instanceof PlayerHuman ||
                (this.player1 instanceof PlayerAI && this.player2 instanceof PlayerAI) : "player1 must be the gui " +
                "player. Except on Ai vs Ai.";
        // TODO: Set current player properly
        this.currentPlayer = player1;
        ShipList shipList = ShipList.fromSize(settings.getPlaygroundSize());
        return new NewGameResult(shipList);
    }

    @Override
    public PlaceShipResult placeShip(ShipPosition pos) {
        return currentPlayer.placeShip(pos);
    }

    @Override
    public PlaceShipResult moveShip(ShipID id, ShipPosition pos) {
        return currentPlayer.moveShip(id, pos);
    }

    @Override
    public boolean deleteShip(ShipID id) {
        return currentPlayer.deleteShip(id);
    }

    @Override
    public StartShootingRes startShooting() {
        if (!this.player1.areAllShipsPlaced()) return StartShootingRes.OWN_NOT_ALL_SHIPS_PLACED;
        if (!this.player2.areAllShipsPlaced()) return StartShootingRes.ENEMY_NOT_ALL_SHIPS_PLACED;
        return StartShootingRes.SHOOTING_ALLOWED;
    }

    @Override
    public TurnResult shoot(Position position) {
        // TODO: Check if player is allowed to shoot
        TurnResult res = this.shoot(this.currentPlayer, position);
        if (!res.isTURN_AGAIN() && !res.isFINISHED())
            this.nextPlayer();
        return res;
    }

    @Override
    public TurnResult getEnemyTurn() {
        while (this.lastTurn == null) {
            try {
                long milliSecondsPause = 100;
                Thread.sleep(milliSecondsPause);
            } catch (InterruptedException e) {
                // TODO: Handle?
                e.printStackTrace();
            }
        }
        TurnResult lastResult = this.lastTurn;
        this.lastTurn = null;
        return lastResult;
    }

    /**
     * Player is shooting at position.
     * Validation checks are made here.
     *
     * @param player   Player which is currently shooting
     * @param position Where the shot is placed
     * @return TurnResult
     */
    private TurnResult shoot(Player player, Position position) {
        TurnResult.Error shootError = player.canShootAt(position);
        TurnResult res;
        if (shootError != TurnResult.Error.NONE)
            res = TurnResult.failure(shootError);
        else {
            res = this.otherPlayer(player).gotHit(position);
            player.update(res.getSHOT_RESULT());
            return res;
        }
        this.lastTurn = res;
        return res;
    }

    /**
     * Call when a player is completely finished with his turn and the other player can turn.
     * switches the current player to the other player;
     */
    private void nextPlayer() {
        this.currentPlayer = this.otherPlayer(this.currentPlayer);
        this.currentPlayer.makeTurn();
    }

    private Player otherPlayer(Player player) {
        if (player == player1) return player2;
        else return player1;
    }


}
