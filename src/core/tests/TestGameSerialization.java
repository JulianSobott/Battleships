package core.tests;

import core.GameManager;
import core.communication_data.GameSettings;
import core.communication_data.LoadGameResult;
import core.serialization.GameData;
import core.serialization.GameSerialization;
import org.junit.jupiter.api.Test;
import player.PlayerAI;
import player.PlayerHuman;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestGameSerialization {

    @Test
    void testSaveGameDummy() {
        GameSerialization.saveGame(getDummyManager());
    }

    @Test
    void testLoadGameDummy() {
        long id = GameSerialization.saveGame(getDummyManager());
        LoadGameResult res = GameSerialization.loadGame(id);
        assertEquals(LoadGameResult.LoadStatus.SUCCESS, res.getStatus());
        assertEquals(id, res.getGameData().getGameID());
    }

    @Test
    void testGetAllSaveGames() {
        GameSerialization.deleteAllSaveGames();
        long id1 = GameSerialization.saveGame(getDummyManager());
        long id2 = GameSerialization.saveGame(getDummyManager());
        List<GameData> data = GameSerialization.getAllSaveGames();
        assertEquals(2, data.size());
        for (GameData d : data) {
            assertTrue(d.getGameID() == id1 || d.getGameID() == id2);
        }
    }

    private GameManager getDummyManager() {
        int size = 10;
        GameSettings settings = new GameSettings(size,
                new PlayerHuman(0, "human", size),
                new PlayerAI(1, "AI", size), null);
        GameManager dummyManager = new GameManager();
        dummyManager.newGame(settings);
        return dummyManager;
    }
}
