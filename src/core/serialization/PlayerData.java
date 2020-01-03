package core.serialization;

import player.PlayerAI;

public class PlayerData {

    private PlayerType type;
    private PlayerAI.Difficulty aiDifficulty;
    private PlaygroundData ownPlayground;
    private PlaygroundData enemyPlayground;
    private String name;
    private int index;

    public PlayerData(PlayerType type, PlayerAI.Difficulty aiDifficulty,
                      PlaygroundData ownPlayground, PlaygroundData enemyPlayground, String name, int index) {
        this.type = type;
        this.aiDifficulty = aiDifficulty;
        this.ownPlayground = ownPlayground;
        this.enemyPlayground = enemyPlayground;
        this.name = name;
        this.index = index;
    }

    public PlayerType getType() {
        return type;
    }

    public PlayerAI.Difficulty getAiDifficulty() {
        return aiDifficulty;
    }

    public PlaygroundData getOwnPlayground() {
        return ownPlayground;
    }

    public PlaygroundData getEnemyPlayground() {
        return enemyPlayground;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }
}
