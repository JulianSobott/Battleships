package core.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import core.GameManager;
import core.Player;
import core.communication_data.LoadGameResult;
import core.utils.logging.LoggerLogic;

import java.io.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Save and load a game.
 */
public class GameSerialization {

    private static final String saveGameFilePattern = "game_(?<id>\\d*).json";
    private static final boolean checkHashesAtValidityCheck = true;
    private static final File folderPath = new File("SaveGames/");

    // TODO: Add ID as parameter

    public static long saveGame(GameManager gameManager) {
        LoggerLogic.info("Saving game ...");
        // init folder
        Thread folderInitThread = new Thread(GameSerialization::initSaveGamesFolder);
        folderInitThread.start();

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
            folderInitThread.join();
            File f = GameSerialization.getFile(gameID);
            if (f.getParentFile().exists() && f.getParentFile().isDirectory()) {
                LoggerLogic.debug(f.getAbsolutePath());
                mapper.writeValue(f, gameData);
                LoggerLogic.info("Successfully saved game: id=" + gameID);
            }else {
                LoggerLogic.error("Can not save game! Reason: Missing folder " + f.getParentFile());
            }
        } catch (IOException | InterruptedException e) {
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

    public static List<GameData> getAllSaveGames() {
        List<GameData> savedGames = new ArrayList<>();
        if (initSaveGamesFolder()) {
            Pattern p = Pattern.compile(saveGameFilePattern);
            for (File f : Objects.requireNonNull(folderPath.listFiles())) {
                Matcher m = p.matcher(f.getName());
                boolean matches = m.matches();
                assert matches: "Invalid regex or file name. " + f;
                long id = Long.parseLong(m.group("id"));
                LoadGameResult res = loadGame(id);
                if (res.getStatus() == LoadGameResult.LoadStatus.SUCCESS)
                    savedGames.add(res.getGameData());
            }
        }
        return savedGames;
    }

    private static File getFile(long gameID) {
        return new File(folderPath, "game_" + gameID + ".json");
    }

    /**
     * Ensures that a folder with valid save games exist.
     *
     * Creates a new folder if non exist.
     * Delete all files, that are not valid save games.
     */
    private static boolean initSaveGamesFolder() {
        // Ensure folder exists
        boolean make_folder = false;
        if (folderPath.exists()) {
            if (folderPath.isFile()) {
                boolean success = folderPath.delete();
                if (!success) LoggerLogic.warning("Can not delete file: file=" + folderPath);
                make_folder = true;
            } else { // Do nothing. Folder already exist
            }
        } else {
            make_folder = true;
        }
        if (make_folder) {
            boolean success = folderPath.mkdir();
            if (!success) LoggerLogic.warning("Can not mkdir: file=" + folderPath);
        }

        if (folderPath.exists() && folderPath.isDirectory()) {
            // Check all files for validity
            for (File f : Objects.requireNonNull(folderPath.listFiles())) {
                if (!GameSerialization.isValidSaveGame(f)) {
                    boolean success = f.delete();
                    if (!success) LoggerLogic.warning("Can not delete file: file=" + f);
                }
            }
            return true;
        } else {
            LoggerLogic.error("No folder for saving games available! Folder does not exist: " + folderPath);
            return false;
        }
    }

    private static boolean isValidSaveGame(File file) {
        // valid file
        if (file == null || !file.exists() || !file.isFile() || !file.canRead()) {
            return false;
        }

        // valid name
        if (!file.getName().matches(GameSerialization.saveGameFilePattern)) {
            return false;
        }

        // valid content
        if (GameSerialization.checkHashesAtValidityCheck) {
            // TODO
        }

        return true;
    }

    /**
     * Deletes all files in the SaveGames folder
     *
     * Only for debug and testing purposes.
     */
    public static void deleteAllSaveGames() {
        if (folderPath.exists() && folderPath.isDirectory()) {
            // Check all files for validity
            for (File f : Objects.requireNonNull(folderPath.listFiles())) {
                //noinspection ResultOfMethodCallIgnored
                f.delete();
            }
        }
    }
}
