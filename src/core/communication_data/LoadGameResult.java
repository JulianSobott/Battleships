package core.communication_data;

import core.serialization.GameData;

public class LoadGameResult {

    private GameData gameData;
    private LoadStatus status;

    public LoadGameResult(GameData gameData, LoadStatus status) {
        this.gameData = gameData;
        this.status = status;
    }

    public enum LoadStatus {
        SUCCESS, INVALID_JSON, INVALID_ID
    }


    public GameData getGameData() {
        return gameData;
    }

    public LoadStatus getStatus() {
        return status;
    }
}
