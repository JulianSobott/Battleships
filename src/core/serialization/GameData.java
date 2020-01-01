package core.serialization;

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
    private PlayerType[] playerTypes;
    private PlayerAI.Difficulty[] aiDifficulties;
    private PlaygroundData ownPlayground;
    private PlaygroundData enemyPlayground;

    public GameData(long gameID, String timestamp, int round, int currentPlayer, PlayerType[] playerTypes,
                    PlayerAI.Difficulty[] aiDifficulties, PlaygroundData ownPlayground,
                    PlaygroundData enemyPlayground) {
        this.gameID = gameID;
        this.timestamp = timestamp;
        this.round = round;
        this.currentPlayer = currentPlayer;
        this.playerTypes = playerTypes;
        this.aiDifficulties = aiDifficulties;
        this.ownPlayground = ownPlayground;
        this.enemyPlayground = enemyPlayground;
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

    public PlayerType[] getPlayerTypes() {
        return playerTypes;
    }

    public PlayerAI.Difficulty[] getAiDifficulties() {
        return aiDifficulties;
    }

    public PlaygroundData getOwnPlayground() {
        return ownPlayground;
    }

    public PlaygroundData getEnemyPlayground() {
        return enemyPlayground;
    }
}
