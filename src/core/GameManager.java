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
    private Player[] players;
    private Player currentPlayer;

    // Player A wants to shoot at position: nextTurns.get(A)
    // Queue must only contain 0 to 1 item.
    private HashMap<Player, ConcurrentLinkedQueue<Position>> nextTurns = new HashMap<>();

    // Player A wants the lastTurns from Player B: lastTurns.get(A).get(B)
    private HashMap<Player, ConcurrentLinkedQueue<TurnResult>> lastTurns = new HashMap<>();
    private HashMap<String, Player> idPlayerHashMap = new HashMap<>();

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
        this.players = new Player[]{settings.getP1(), settings.getP2()};
        for(Player p1 : this.players){
            this.lastTurns.put(p1, new ConcurrentLinkedQueue<>());
            this.nextTurns.put(p1, new ConcurrentLinkedQueue<>());
        }
        // TODO: find better way
        idPlayerHashMap.put("GUI_1", this.player1);
        idPlayerHashMap.put("AI_2", this.player1);

        // TODO: Set current player properly
        this.currentPlayer = player1;
        ShipList shipList = ShipList.fromSize(settings.getPlaygroundSize());
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
        LoggerLogic.debug("Make shot: player=" + player + " position=" + position);
        synchronized (this.nextTurns.get(player)) {
            LoggerLogic.debug("" + this.nextTurns);
            LoggerLogic.debug("" + this.nextTurns.get(player));
            LoggerLogic.debug("" + position);
            if (this.nextTurns.get(player).size() != 1) {
                this.nextTurns.get(player).add(position);
                this.nextTurns.get(player).notifyAll();
                LoggerLogic.debug("Notify on Queue: " + this.nextTurns.get(player));
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
                    e.printStackTrace();
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
        Thread mainGameLoop = new Thread(() -> {
            TurnResult res;
            do {
                res = turnLoop();
                nextPlayer();
            } while (!res.isFINISHED());
        });
        mainGameLoop.setName("Main_gameLoop");
        mainGameLoop.start();
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
            this.saveTurnResult(res);
        } while (res.isTURN_AGAIN());
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
    }

    private Position getPlayerShootPosition() {
        synchronized (this.nextTurns.get(this.currentPlayer)) {
            while (this.nextTurns.get(this.currentPlayer).isEmpty()) {
                try {
                    LoggerLogic.debug("Queue "+ this.nextTurns.get(this.currentPlayer) +" was empty: waiting...");
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
