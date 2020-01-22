package core;

import core.communication_data.*;
import core.playgrounds.Playground;
import core.serialization.GameSerialization;
import core.utils.logging.LoggerLogic;
import core.utils.logging.LoggerState;
import player.PlayerAI;
import player.PlayerHuman;
import player.PlayerNetwork;

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GameManager implements GameManagerInterface {

    // TODO: Check synchronize stuff for correctness
    // TODO: Remove deprecated methods/attributes
    // TODO: Better names for methods: shoot, makeShoot, turn, ...
    private Player player1;
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

    private void initGame(Player p1, Player p2) {
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
        return ((PlayerHuman)player1).placeShip(pos);
    }

    @Override
    public PlaceShipsRandomRes placeShipsRandom() {
        return ((PlayerHuman)player1).placeShipsRandom();
    }

    @Override
    public PlaceShipResult moveShip(ShipID id, ShipPosition pos) {
        return ((PlayerHuman)player1).moveShip(id, pos);
    }

    @Override
    public boolean deleteShip(ShipID id) {
        return ((PlayerHuman)player1).deleteShip(id);
    }

    /**
     * A GUI player wants to make a shot.
     * The information is stored and used when the GameManager is ready to use this.
     * Nothing is returned. Instead the GUI should call {@link #pollTurn(String)} to get the results of his own and the
     * enemy turns.
     * Validity of the turn is checked in {@link #turn(Player, Position)} and stored in the TurnResult.
     *
     * @param player The Player that wants to make a shot
     * @param position The target position
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
        return player == this.currentPlayer;
    }

    /**
     * A Player wants to make a turn and shoot at a field.
     * Validity checks are made here.
     *
     * @param player The Player that makes the turn.
     * @param position Target of the shoot.
     * @return A TurnResult with if information if the turn was valid and if so what was hit.
     */
    private TurnResult turn(Player player, Position position) {
        LoggerLogic.info("turn: player=" + player + ", position=" + position);
        TurnResult res;
        TurnResult.Error shootError;
        if (!this.isAllowedToShoot(player)) {
            res = TurnResult.failure(player, TurnResult.Error.NOT_YOUR_TURN);
        }
        else if ((shootError = player.canShootAt(position)) != TurnResult.Error.NONE) {
            res = TurnResult.failure(player, shootError);
        }
        else {
            ShotResult resShot = this.otherPlayer(player).gotHit(position);
            player.update(resShot);
            boolean isFinished = player.allEnemyShipsSunken();
            boolean shootAgain = resShot.getType() == Playground.FieldType.SHIP && !isFinished;
            res = new TurnResult(player, resShot, shootAgain, isFinished);
        }
        LoggerLogic.info("turn result: TurnResult=" + res);
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
                if (res != null && !res.isFINISHED()) {
                    round++;
                    nextPlayer();
                }
                else { // game was interrupted
                }
            } while (res != null && !res.isFINISHED() && !Thread.currentThread().isInterrupted());
            LoggerLogic.info("End main gameLoop");
            if(res != null && res.isFINISHED()) {
                LoggerState.info("Winner after " + round + " rounds: " + players[res.getPlayerIndex()].toString());
            }
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
        for(Player p : this.players){
            synchronized (this.lastTurns.get(p)){
                if (p instanceof PlayerHuman || (player1 instanceof PlayerAI && player1 == p)) {
                    this.lastTurns.get(p).add(res);
                    this.lastTurns.get(p).notifyAll();
                }
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

    public long saveGame(long id) {
        return GameSerialization.saveGame(this, id);
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
