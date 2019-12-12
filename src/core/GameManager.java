package core;

import core.communication_data.*;
import core.utils.logging.LoggerLogic;
import core.utils.logging.LoggerState;

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GameManager implements GameManagerInterface {

    // TODO: Check synchronize stuff for correctness
    // TODO: Remove deprecated methods/attributes
    // TODO: Better names for methods: shoot, makeShoot, turn, ...
    private Player player1, player2;
    private Player currentPlayer;

    private ConcurrentLinkedQueue<TurnResult> lastTurnsP1 = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<TurnResult> lastTurnsP2 = new ConcurrentLinkedQueue<>();
    private HashMap<Player, ConcurrentLinkedQueue<Position>> nextTurns = new HashMap<>();
    private HashMap<Player, ConcurrentLinkedQueue<TurnResult>> lastTurns = new HashMap<>();


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
        lastTurns.put(this.player1, new ConcurrentLinkedQueue<>());
        lastTurns.put(this.player2, new ConcurrentLinkedQueue<>());
        nextTurns.put(this.player1, new ConcurrentLinkedQueue<>());
        nextTurns.put(this.player2, new ConcurrentLinkedQueue<>());
        return new NewGameResult(shipList);
    }

    public void startGame() {
        this.gameLoop();
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

    /**
     * TODO: difference between shoot?
     *
     * @param player
     * @param position
     */
    private void makeShot(Player player, Position position) {
        synchronized (this.nextTurns.get(player)) {
            LoggerLogic.debug("" + this.nextTurns);
            LoggerLogic.debug("" + this.nextTurns.get(player));
            LoggerLogic.debug("" + position);
            if (this.nextTurns.get(player).size() != 1) {
                this.nextTurns.get(player).add(position);
                this.nextTurns.get(player).notify();
            } else {
                LoggerLogic.debug("Player has already made a shot. player=" + player);
            }
        }
    }

    public void shootP1(Position pos) {
        makeShot(this.player1, pos);
    }

    public void shootP2(Position pos) {
        this.makeShot(this.player2, pos);
    }

    private TurnResult getTurn(Player player) {
        synchronized (this.lastTurns.get(player)) {
            while (this.lastTurns.get(player).isEmpty()) {
                try {
                    this.lastTurns.get(player).wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            LoggerLogic.debug("Returning player 2 Result:");
            return this.lastTurns.get(player).poll();
        }
    }


    private boolean isAllowedToShoot(Player player) {
        if (player != this.currentPlayer) return false;
        return true;
    }

    public TurnResult getTurnPlayer2() {
        return this.getTurn(this.player2);
    }

    public TurnResult getTurnPlayer1() {
        return this.getTurn(this.player1);
    }

    private TurnResult turn(Player player, Position position) {
        LoggerLogic.info("turn: player=" + player + ", position=" + position);
        TurnResult res;
        if (!this.isAllowedToShoot(player)) {
            res = TurnResult.failure(TurnResult.Error.NOT_YOUR_TURN);
        } else {
            res = this.shoot(player, position);
        }
        LoggerLogic.info("Enemy playground from player: player=" + player);
        player.playgroundEnemy.printField();
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
            boolean isFinished = player.allEnemyShipsSunken();
            boolean shootAgain = resShot.getType() == Playground.FieldType.SHIP && !isFinished;
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

    /**
     * Loop until game is finished
     */
    private void gameLoop() {
        new Thread(() -> {
            TurnResult res;
            do {
                res = turnLoop();
                nextPlayer();
            } while (!res.isFINISHED());
        }).start();
    }

    /**
     * Loop until a player finishes his turn.
     *
     * @return The last TurnResult
     */
    private TurnResult turnLoop() {
        TurnResult res;
        do {
            // TODO: Find better solution for makeTurn. maybe flag in player: triggerMakeTurn needed.
            Position pos = this.currentPlayer.makeTurn();
            if (pos == null) {
                pos = this.getPlayerShootPosition();
            }
            res = this.turn(this.currentPlayer, pos);
            synchronized (this.lastTurns.get(this.currentPlayer)) {
                this.lastTurns.get(this.currentPlayer).add(res);
                this.lastTurns.get(this.currentPlayer).notify();
            }
        } while (res.isTURN_AGAIN());
        return res;
    }

    private Position getPlayerShootPosition() {
        synchronized (this.nextTurns.get(this.currentPlayer)) {
            while (this.nextTurns.get(this.currentPlayer).isEmpty()) {
                try {
                    LoggerLogic.debug("Queue was empty: waiting...");
                    this.nextTurns.get(this.currentPlayer).wait();
                    LoggerLogic.debug("Queue is no longer empty");
                } catch (InterruptedException e) {
                    LoggerLogic.error("Thread that waited for shot was interrupted. Error=" + e.getMessage());
                    e.printStackTrace();
                }
            }
            return this.nextTurns.get(this.currentPlayer).poll();
        }
    }

    private Player otherPlayer(Player player) {
        if (player == player1) return player2;
        else return player1;
    }
}
