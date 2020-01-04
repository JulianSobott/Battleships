package core.serialization;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
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
    private Player currentPlayer;     // 0 or 1
    private Player[] players;

    public GameData() { // Jackson deserialization
    }

    public GameData(long gameID, String timestamp, int round, Player currentPlayer, Player[] players) {
        this.gameID = gameID;
        this.timestamp = timestamp;
        this.round = round;
        this.currentPlayer = players[currentPlayer.getIndex()];
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

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Player[] getPlayers() {
        return players;
    }

    public void setGameID(long gameID) {
        this.gameID = gameID;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public void setPlayers(Player[] players) {
        this.players = players;
    }
}
