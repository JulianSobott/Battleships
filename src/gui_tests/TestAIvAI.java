package gui_tests;

import core.GameManager;
import core.Player;
import core.communication_data.GameSettings;
import gui.PlayGame.ControllerPlayGame;
import gui.ShipPlacement.ControllerShipPlacement;
import gui.WindowChange.SceneLoader;
import javafx.application.Application;
import javafx.stage.Stage;
import player.PlayerAI;
import player.PlayerHuman;

import java.util.ArrayList;


public class TestAIvAI extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        int playgroundSize = 30;
        Player p1 = new PlayerAI(0, "AI1", playgroundSize, PlayerAI.Difficulty.HARD);
        Player p2 = new PlayerAI(1, "AI2", playgroundSize, PlayerAI.Difficulty.EASY);
        GameSettings settings = new GameSettings(playgroundSize, p1, p2, null, p1, true);

        ControllerShipPlacement controllerShipPlacement = new ControllerShipPlacement(settings);
        SceneLoader sceneLoader = new SceneLoader(null, "../../gui/ShipPlacement/ShipPlacement.fxml",
                controllerShipPlacement);
        sceneLoader.loadSceneInNewWindow("AI vs AI");
    }
}
