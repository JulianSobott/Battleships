package core;

import core.communication_data.*;
import core.utils.Logger;

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
    public NewGameResult newGame(GameSettings settings) {
        this.player1 = settings.getP1();
        this.player2 = settings.getP2();
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
    public PlaceShipsRandomRes placeShipsRandom() {
        return this.currentPlayer.placeShipsRandom();
    }

    @Override
    public PlaceShipResult moveShip(ShipID id, ShipPosition pos) {
        return currentPlayer.moveShip(id, pos);
    }

    @Override
    public boolean deleteShip(ShipID id) {
        return currentPlayer.deleteShip(id);
    }


    public TurnResult shootP1(Position position) {
        if(this.isAllowedToShoot(this.player1)){
            return TurnResult.failure(TurnResult.Error.NOT_YOUR_TURN);
        }else{
            TurnResult res = this.shoot(this.player1, position);
            if (!res.isTURN_AGAIN() && !res.isFINISHED()){
                Logger.info("Theoretical move of next player");
                // TODO start in new thread
                //this.nextPlayer();
            }else{
                // TODO?
            }
            return res;
        }

    }

    public TurnResult shootP2(Position position) {
        // TODO: Check if player is allowed to shoot
        TurnResult res = this.shoot(this.currentPlayer, position);
        if (!res.isTURN_AGAIN() && !res.isFINISHED())
            this.nextPlayer();
        return res;
    }

    private boolean isAllowedToShoot(Player player1) {
        if(this.player1 != this.currentPlayer) return false;
        return true;
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
            ShotResult resShot = this.otherPlayer(player).gotHit(position);
            player.update(resShot);
            boolean shootAgain = resShot.getType() == Playground.FieldType.SHIP;
            boolean isFinished = player.allEnemyShipsSunken();
            res = new TurnResult(resShot, shootAgain, isFinished);
        }
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
