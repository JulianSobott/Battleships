package core.tests;

import core.GameManager;
import core.communication_data.GameSettings;
import core.serialization.GameSerialization;
import org.junit.jupiter.api.Test;
import player.PlayerAI;
import player.PlayerHuman;

public class TestGameSerialization {

    @Test
    void testSaveGameDummy() {
        GameSerialization gameSerialization = new GameSerialization();
        int size = 10;
        GameSettings settings = new GameSettings(size,
                new PlayerHuman(0, "human", size),
                new PlayerAI(1, "AI", size));
        GameManager dummyManager = new GameManager();
        dummyManager.newGame(settings);
        gameSerialization.saveGame(dummyManager);
    }

    @Test
    void testLoadGameDummy() {
        GameSerialization gameSerialization = new GameSerialization();
        int size = 10;
        GameSettings settings = new GameSettings(size,
                new PlayerHuman(0, "human", size),
                new PlayerAI(1, "AI", size));
        GameManager dummyManager = new GameManager();
        dummyManager.newGame(settings);
        long id = gameSerialization.saveGame(dummyManager);
        gameSerialization.loadGame(id);
    }
}
