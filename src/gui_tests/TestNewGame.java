package gui_tests;

import gui.WindowChange.SceneLoader;
import javafx.application.Application;
import javafx.stage.Stage;

public class TestNewGame extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        SceneLoader.loadSceneInNewWindow(SceneLoader.GameScene.NEW_GAME_TEST, null, "Test new game");
    }
}
