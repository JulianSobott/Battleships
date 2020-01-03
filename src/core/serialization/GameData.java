package core.serialization;

import core.Player;
import player.PlayerAI;

/**
 * All data that need to be saved.
 */
public class GameData {

    // Metadata

    private long gameID;
    private String timestamp;

    // Game data

    private int round;
    private int currentPlayer;     // 0 or 1
    private Player[] players;

    public GameData(long gameID, String timestamp, int round, int currentPlayer, Player[] players) {
        this.gameID = gameID;
        this.timestamp = timestamp;
        this.round = round;
        this.currentPlayer = currentPlayer;
        this.players = players;
    }

    public long getGameID() {
        return gameID;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getRound() {
        return round;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public Player[] getPlayers() {
        return players;
    }
}
