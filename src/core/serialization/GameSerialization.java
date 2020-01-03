package core.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import core.GameManager;
import core.Player;
import core.utils.logging.LoggerLogic;
import player.PlayerAI;

import java.io.File;
import java.io.IOException;
import java.time.Instant;

/**
 * Save and load a game.
 */
public class GameSerialization {

    public long saveGame(GameManager gameManager) {
        // collect data
        long gameID = System.currentTimeMillis();
        String timestamp = Instant.now().toString();
        int round = gameManager.getRound();
        int currentPlayerIndex = gameManager.getCurrentPlayer().getIndex();
        Player[] players = gameManager.getPlayers();
//        PlayerData[] playersData = new PlayerData[players.length];
//        int i = 0;
//        for (Player p : players) {
//            PlayerType type = PlayerType.fromPlayer(p);
//            PlayerAI.Difficulty difficulty = type == PlayerType.AI ? ((PlayerAI) p).getDifficulty() : null;
//            PlaygroundData playgroundDataOwn = PlaygroundData.fromPlayground(p.get)
//            PlayerData data = new PlayerData(type, difficulty, )
//            i++;
//        }

        // save data in object
        GameData gameData = new GameData(gameID, timestamp, round, currentPlayerIndex, players);
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        // write data to json file
        try {
            File f = new File("game_" + gameID + ".json");
            LoggerLogic.debug(f.getAbsolutePath());
            mapper.writeValue(f, gameData);
            LoggerLogic.info("Successfully saved game: id=" + gameID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return gameID;
    }

    public void loadGame(int id) {

    }
}
