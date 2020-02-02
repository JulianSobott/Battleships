package gui_tests;

import core.communication_data.GameSettings;
import gui.ShipPlacement.ControllerShipPlacement;
import gui.WindowChange.SceneLoader;
import javafx.application.Application;
import javafx.stage.Stage;
import player.PlayerAI;
import player.PlayerHuman;

public class TestMain extends Application{
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        int playgroundSize = 11;
        GameSettings settings = new GameSettings(playgroundSize, new PlayerHuman(0, "1", playgroundSize),
                new PlayerAI(1, "2", playgroundSize, PlayerAI.Difficulty.HARD));
        ControllerShipPlacement controller = new ControllerShipPlacement(settings);
        SceneLoader.loadSceneInNewWindow(SceneLoader.GameScene.SHIP_PLACEMENT, controller, "Test Ship Placement");
    }
}
