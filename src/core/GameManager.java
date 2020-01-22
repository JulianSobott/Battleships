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

    private Player player1;
    private Player player2;
    private Player[] players;
    private Player currentPlayer;

    private int round = 1;

    // Player A wants the lastTurn: lastTurns.get(A)
    private HashMap<Player, ConcurrentLinkedQueue<TurnResult>> lastTurns = new HashMap<>();
    private HashMap<String, Player> idPlayerHashMap = new HashMap<>();

    private Thread inGameThread;

    /**
     * Constructor, when game was loaded
     * @param players All Players
     * @param currentPlayer The player that was the currentPlayer, when the game was saved
     * @param round The round in which the game was saved
     */
    public GameManager(Player[] players, Player currentPlayer, int round) {
        this.initGame(players[0], players[1]);
        this.currentPlayer = currentPlayer;
        this.round = round;
    }

    /**
     * Constructor, when attributes will be set in the {@link #newGame(GameSettings) newGame} method.
     */
    public GameManager() {
    }

    @Override
    public NewGameResult newGame(GameSettings settings) {
        this.initGame(settings.getP1(), settings.getP2());
        this.currentPlayer = settings.getStartingPlayer();
        ShipList shipList = ShipList.fromSize(settings.getPlaygroundSize());
        return new NewGameResult(shipList);
    }

    /**
     * Set attributes and init queues and maps.
     * @param p1 PlayerHuman or PlayerAI
     * @param p2 PlayerAI or PlayerNetwork
     */
    private void initGame(Player p1, Player p2) {
        this.player1 = p1;
        this.player2 = p2;
        this.players = new Player[]{p1, p2};
        for(Player p : this.players){
            this.lastTurns.put(p, new ConcurrentLinkedQueue<>());
        }
        // TODO: find better way
        idPlayerHashMap.put("GUI_1", this.player1);
        idPlayerHashMap.put("AI_2", this.player2);
    }

    /**
     * Asks if all ships are placed.
     *
     * @return Enum with information who who is not ready.
     */
    public StartShootingRes allPlayersReady() {
        if (!this.player1.areAllShipsPlaced()) return StartShootingRes.OWN_NOT_ALL_SHIPS_PLACED;
        if (!this.player2.areAllShipsPlaced()) return StartShootingRes.ENEMY_NOT_ALL_SHIPS_PLACED;
        return StartShootingRes.SHOOTING_ALLOWED;
    }

    /**
     * Starts the {@link #gameLoop()} which is responsible for the main turn logic.
     */
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
     * The GUI player wants to make a shot.
     * The information is stored and used when the GameManager is ready to use this.
     * Nothing is returned. Instead the GUI should call {@link #pollTurn(String)} to get the results of his own and the
     * enemy turns.
     * Validity of the turn is checked in {@link #turn(Player, Position)} and stored in the TurnResult.
     *
     * @param pos The target position
     */
    public void shootP1(Position pos) {
        assert player1 instanceof PlayerHuman: "Only PLayerHuman can make a 'external' Shot";
        ((PlayerHuman)player1).addNextShot(pos);
    }

    /**
     * Poll the last turn that happened.
     * Turn can be from both players. TurnResult can also indicate an invalid turn.
     * If no turn happened till now this method blocks until a turn happens.
     * Once a turn is polled it is removed from the list and can't be polled from the same id again.
     * For other id's it is still available.
     *
     * @param id ID that matches to a player. The id must match to the PLayer that wants the turn.
     *           id's see {@link #initGame(Player, Player)}
     * @return A TurnResult. Results are stored in a FIFO-Queue.
     */
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
            assert res != null: "Result shouldn't be possible to be null";
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
            Position pos = this.currentPlayer.makeTurn();
            LoggerLogic.debug("Position=" + pos + ", Player=" + this.currentPlayer);
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
     * @param res The TurnResult that is to save.
     */
    private void saveTurnResult(TurnResult res){
        for(Player p : this.players){
            synchronized (this.lastTurns.get(p)){
                if (p instanceof PlayerHuman || (player1 instanceof PlayerAI && player1 == p)) {
                    this.lastTurns.get(p).add(res);
                    this.lastTurns.get(p).notifyAll();
                    LoggerLogic.debug("SavedResult: res=" + res + ". lastTurns size=" + this.lastTurns.get(p).size());
                }
            }
        }
    }

    private Player otherPlayer(Player player) {
        if (player == player1) return player2;
        else return player1;
    }


    /**
     * Stops the inGameThread and waits till it is joined.
     */
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
