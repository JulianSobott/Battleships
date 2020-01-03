package core.serialization;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import core.GameManager;
import core.Player;
import core.utils.logging.LoggerLogic;
import player.PlayerAI;

import java.io.*;
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

        // save data in object
        GameData gameData = new GameData(gameID, timestamp, round, currentPlayerIndex, players);
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

    public void loadGame(long id) {
        LoggerLogic.info("Loading game: id=" + id);
        ObjectMapper mapper = new ObjectMapper();
        File f = GameSerialization.getFile(id);
        try {
            InputStream fileInputStream = new FileInputStream(f.getAbsolutePath());
            GameData gameData = mapper.readValue(fileInputStream, GameData.class);
            // TODO: set in GameManager
            LoggerLogic.info("Successfully loaded game");
        } catch (FileNotFoundException e) {
            LoggerLogic.warning("No game found with id=" + id + ". TODO what TODO");
        } catch (IOException e) {
            LoggerLogic.warning("Can't read saved json data. TODO what TODO");
            e.printStackTrace();
        }
    }

    private static File getFile(long gameID) {
        return new File("game_" + gameID + ".json");
    }
}
