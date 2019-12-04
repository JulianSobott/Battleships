package core;

import core.communication_data.*;
import core.utils.logging.LoggerLogic;
import core.utils.logging.LoggerState;

import java.util.LinkedList;
import java.util.Queue;

public class GameManager implements GameManagerInterface {

    private Player player1, player2;
    private Player currentPlayer;

    private Queue<TurnResult> lastTurnsP2 = new LinkedList<>(), lastTurnsP1 = new LinkedList<>();


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


    public TurnResult shootP1(Position pos){
        TurnResult res = this.turn(this.player1, pos);
        lastTurnsP1.add(res);
        if (!res.isTURN_AGAIN() && !res.isFINISHED()){
            // TODO start in new thread
            this.nextPlayer();
            this.player2Turn();
        }else{
            // TODO?
        }
        return res;
    }

    public TurnResult shootP2(Position pos){
        TurnResult res = this.turn(this.player2, pos);
        this.lastTurnsP2.add(res);
        return res;
    }


    private boolean isAllowedToShoot(Player player) {
        if(player != this.currentPlayer) return false;
        return true;
    }

    public TurnResult getTurnPlayer2() {
        while (this.lastTurnsP2.isEmpty()) {
            try {
                long milliSecondsPause = 100;
                Thread.sleep(milliSecondsPause);
            } catch (InterruptedException e) {
                // TODO: Handle?
                e.printStackTrace();
            }
        }
        return this.lastTurnsP2.poll();
    }

    private TurnResult turn(Player player, Position position){
        LoggerLogic.info("turn: player=" + player + ", position=" + position);
        TurnResult res;
        if(!this.isAllowedToShoot(player)){
            res = TurnResult.failure(TurnResult.Error.NOT_YOUR_TURN);
        }else{
            res = this.shoot(player, position);
        }
        LoggerLogic.info("turn result: TurnResult=" + res);
        return res;
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
        LoggerState.info("Next Player: " + this.currentPlayer);
    }

    private void player2Turn(){
        TurnResult res;
        do {
            Position pos = this.currentPlayer.makeTurn();
            res = this.shootP2(pos);
            LoggerLogic.info("player2Turn result: " + res);
        }while (res.isTURN_AGAIN());
        // TODO: handle game_over
        this.nextPlayer();
    }

    private Player otherPlayer(Player player) {
        if (player == player1) return player2;
        else return player1;
    }
}
