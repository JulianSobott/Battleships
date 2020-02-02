package gui_tests;

import core.communication_data.LoadGameResult;
import core.serialization.GameData;
import core.serialization.GameSerialization;
import core.utils.logging.LoggerGUI;
import gui.PlayGame.ControllerPlayGame;
import gui.WindowChange.SceneLoader;
import javafx.application.Application;
import javafx.stage.Stage;


public class TestLoad extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        int gameID = 1;
        LoadGameResult res = GameSerialization.loadGame(gameID);
        if (res.getStatus() == LoadGameResult.LoadStatus.SUCCESS) {
            GameData gameData = res.getGameData();
            ControllerPlayGame controller = ControllerPlayGame.fromLoad(gameData);
            SceneLoader.loadSceneInNewWindow(SceneLoader.GameScene.PLAY_GAME, controller, "Test load");
            controller.initFieldsFromLoad(gameData);
        } else {
            LoggerGUI.error("Load failed: res=" + res);
        }
    }
}
