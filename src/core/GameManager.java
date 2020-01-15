package core;

import core.communication_data.*;
import core.serialization.GameData;
import core.serialization.GameSerialization;
import core.utils.logging.LoggerLogic;
import core.utils.logging.LoggerState;
import player.PlayerHuman;
import player.PlayerNetwork;
import player.PlayerPlacer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GameManager implements GameManagerInterface {

    // TODO: Check synchronize stuff for correctness
    // TODO: Remove deprecated methods/attributes
    // TODO: Better names for methods: shoot, makeShoot, turn, ...
    private PlayerHuman player1;
    private Player player2;
    private Player[] players;
    private Player currentPlayer;

    // Player A wants to shoot at position: nextTurns.get(A)
    // Queue must only contain 0 to 1 item.
    private HashMap<Player, ConcurrentLinkedQueue<Position>> nextTurns = new HashMap<>();
    private int round = 1;

    // Player A wants the lastTurns from Player B: lastTurns.get(A).get(B)
    private HashMap<Player, ConcurrentLinkedQueue<TurnResult>> lastTurns = new HashMap<>();
    private HashMap<String, Player> idPlayerHashMap = new HashMap<>();

    private Thread inGameThread;

    /**
     * Constructor, when game was loaded
     * @param players
     * @param currentPlayer
     * @param round
     */
    public GameManager(Player[] players, Player currentPlayer, int round) {
        this.initGame((PlayerHuman) players[0], players[1]);
        this.currentPlayer = currentPlayer;
        this.round = round;
    }

    /**
     * Constructor, when attributes will be set in the {@link #newGame(GameSettings) newGame} method.
     */
    public GameManager() {
    }

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
        this.initGame(settings.getP1(), settings.getP2());
        this.currentPlayer = settings.getStartingPlayer();

        ShipList shipList = ShipList.fromSize(settings.getPlaygroundSize());
        return new NewGameResult(shipList);
    }

    private void initGame(PlayerHuman p1, Player p2) {
        this.player1 = p1;
        this.player2 = p2;
        this.players = new Player[]{p1, p2};
        for(Player p : this.players){
            this.lastTurns.put(p, new ConcurrentLinkedQueue<>());
            this.nextTurns.put(p, new ConcurrentLinkedQueue<>());
        }
        // TODO: find better way
        idPlayerHashMap.put("GUI_1", this.player1);
        idPlayerHashMap.put("AI_2", this.player2);
    }

    public StartShootingRes startShooting() {
        // TODO: return or wait till both finished? new thread? User should be somehow informed what is going on.
        if (!this.player1.areAllShipsPlaced()) return StartShootingRes.OWN_NOT_ALL_SHIPS_PLACED;
        if (!this.player2.areAllShipsPlaced()) return StartShootingRes.ENEMY_NOT_ALL_SHIPS_PLACED;
        return StartShootingRes.SHOOTING_ALLOWED;
    }

    public void startGame() {
        this.gameLoop();
    }

    @Override
    public PlaceShipResult placeShip(ShipPosition pos) {
        return player1.placeShip(pos);
    }

    @Override
    public PlaceShipsRandomRes placeShipsRandom() {
        return player1.placeShipsRandom();
    }

    @Override
    public PlaceShipResult moveShip(ShipID id, ShipPosition pos) {
        return player1.moveShip(id, pos);
    }

    @Override
    public boolean deleteShip(ShipID id) {
        return player1.deleteShip(id);
    }

    /**
     * TODO: difference between shoot?
     *
     * @param player
     * @param position
     */
    private void makeShot(Player player, Position position) {
        LoggerLogic.debug("Make shot: player=" + player + " position=" + position);
        synchronized (this.nextTurns.get(player)) {
            LoggerLogic.debug("" + this.nextTurns);
            LoggerLogic.debug("" + position);
            if (this.nextTurns.get(player).size() != 1) {
                this.nextTurns.get(player).add(position);
                this.nextTurns.get(player).notifyAll();
                LoggerLogic.debug("Notify on Queue: " + this.nextTurns);
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

    public TurnResult pollTurn(String id) {
        Player player = this.idPlayerHashMap.get(id);
        synchronized (this.lastTurns.get(player)) {
            while (this.lastTurns.get(player).isEmpty()) {
                try {
                    this.lastTurns.get(player).wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }
            TurnResult res = this.lastTurns.get(player).poll();
            LoggerLogic.debug("Returning player " + res.getPlayerIndex() +" Result:");
            return res;
        }
    }


    private boolean isAllowedToShoot(Player player) {
        if (player != this.currentPlayer) return false;
        return true;
    }

    private TurnResult turn(Player player, Position position) {
        LoggerLogic.info("turn: player=" + player + ", position=" + position);
        TurnResult res;
        if (!this.isAllowedToShoot(player)) {
            res = TurnResult.failure(player, TurnResult.Error.NOT_YOUR_TURN);
        } else {
            res = this.shoot(player, position);
        }
        LoggerLogic.info("Enemy playground from player: player=" + player);
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
            res = TurnResult.failure(player, shootError);
        else {
            ShotResult resShot = this.otherPlayer(player).gotHit(position);
            player.update(resShot);
            boolean isFinished = player.allEnemyShipsSunken();
            boolean shootAgain = resShot.getType() == Playground.FieldType.SHIP && !isFinished;
            res = new TurnResult(player, resShot, shootAgain, isFinished);
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
        inGameThread = new Thread(() -> {
            TurnResult res;
            LoggerState.info("Starting player: " + this.currentPlayer);
            do {
                res = turnLoop();
                if (res != null)
                    nextPlayer();
                else { // game was interrupted
                }
            } while (res != null && !res.isFINISHED() && !Thread.currentThread().isInterrupted());
            LoggerLogic.info("End main gameLoop");
        });
        inGameThread.setName("Main_gameLoop");
        inGameThread.start();
        LoggerLogic.info("Started Main_gameLoop thread");
    }

    /**
     * Loop until a player finishes his turn.
     *
     * @return The last TurnResult
     */
    private TurnResult turnLoop() {
        TurnResult res = null;
        do {
            // TODO: Find better solution for makeTurn. maybe flag in player: triggerMakeTurn needed.
            Position pos = this.currentPlayer.makeTurn();
            LoggerLogic.debug("Position=" + pos + ", Player=" + this.currentPlayer);
            if (pos == null) {
                pos = this.getPlayerShootPosition();
                LoggerLogic.debug("Position=" + pos + ", Player=" + this.currentPlayer);
            }
            if (pos != null) {
                res = this.turn(this.currentPlayer, pos);
                LoggerLogic.debug("res=" + res + ", Player=" + this.currentPlayer);
                this.saveTurnResult(res);
            }
        } while (res != null && res.isTURN_AGAIN() && !Thread.currentThread().isInterrupted());
        return res;
    }

    /**
     * Saves the TurnResult everywhere, where it is needed.
     *
     * @param res
     */
    private void saveTurnResult(TurnResult res){
        // TODO: Synchronize on which object
        for(Player p : this.players){
            synchronized (this.lastTurns.get(p)){
                this.lastTurns.get(p).add(res);
                this.lastTurns.get(p).notifyAll();
                // TODO: Poll from all Queues Also AI
            }
        }
        LoggerLogic.debug("SavedResults: lastTurns=" + this.lastTurns);
    }

    private Position getPlayerShootPosition() {
        synchronized (this.nextTurns.get(this.currentPlayer)) {
            while (this.nextTurns.get(this.currentPlayer).isEmpty()) {
                try {
                    LoggerLogic.debug("Queue "+ this.nextTurns.get(this.currentPlayer) +" was empty: waiting...");
                    this.nextTurns.get(this.currentPlayer).wait();
                    LoggerLogic.debug("Queue is no longer empty");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }
            return this.nextTurns.get(this.currentPlayer).poll();
        }
    }

    private Player otherPlayer(Player player) {
        if (player == player1) return player2;
        else return player1;
    }


    public void exitInGameThread() {
        inGameThread.interrupt();
        try {
            this.inGameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Save game
    public long saveGame() {
        long id = GameSerialization.saveGame(this);
        if (this.player2 instanceof PlayerNetwork) {
            ((PlayerNetwork)this.player2).sendSaveGame(id);
        }
        return id;
    }

    // Getters

    public Player[] getPlayers() {
        return players;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public int getRound() {
        return round;
    }
}
