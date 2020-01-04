package core.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import core.GameManager;
import core.Player;
import core.communication_data.LoadGameResult;
import core.utils.logging.LoggerLogic;

import java.io.*;
import java.time.Instant;

/**
 * Save and load a game.
 */
public class GameSerialization {

    public static long saveGame(GameManager gameManager) {
        // collect data
        long gameID = System.currentTimeMillis();
        String timestamp = Instant.now().toString();
        int round = gameManager.getRound();
        Player currentPlayer = gameManager.getCurrentPlayer();
        Player[] players = gameManager.getPlayers();

        // save data in object
        GameData gameData = new GameData(gameID, timestamp, round, currentPlayer, players);
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        // write data to json file
        try {
            File f = GameSerialization.getFile(gameID);
            LoggerLogic.debug(f.getAbsolutePath());
            mapper.writeValue(f, gameData);
            LoggerLogic.info("Successfully saved game: id=" + gameID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return gameID;
    }

    public static LoadGameResult loadGame(long id) {
        LoggerLogic.info("Loading game: id=" + id);
        ObjectMapper mapper = new ObjectMapper();
        File f = GameSerialization.getFile(id);
        GameData gameData = null;
        LoadGameResult.LoadStatus status;
        try {
            InputStream fileInputStream = new FileInputStream(f.getAbsolutePath());
            gameData = mapper.readValue(fileInputStream, GameData.class);
            status = LoadGameResult.LoadStatus.SUCCESS;
            LoggerLogic.info("Successfully loaded game");

        } catch (FileNotFoundException e) {
            LoggerLogic.warning("No game found with id=" + id);
            status = LoadGameResult.LoadStatus.INVALID_ID;
        } catch (IOException e) {
            LoggerLogic.warning("Can't read saved json data");
            e.printStackTrace();
            status = LoadGameResult.LoadStatus.INVALID_JSON;
        }
        LoadGameResult result = new LoadGameResult(gameData, status);
        LoggerLogic.info("LoadGame result=" + result);
        return result;
    }

    private static File getFile(long gameID) {
        return new File("game_" + gameID + ".json");
    }
}
