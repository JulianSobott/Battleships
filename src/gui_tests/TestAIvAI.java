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
        int playgroundSize = 5;
        Player p1 = new PlayerAI(0, "AI1", playgroundSize, PlayerAI.Difficulty.EASY);
        Player p2 = new PlayerAI(1, "AI2", playgroundSize, PlayerAI.Difficulty.MEDIUM);
        GameManager manager = new GameManager();
        manager.newGame(new GameSettings(playgroundSize, p1, p2));
        ControllerPlayGame controllerPlayGame = new ControllerPlayGame(playgroundSize, new ArrayList<>(), manager);
        SceneLoader sceneLoader = new SceneLoader(null, "../../gui/PlayGame/PlayGame.fxml", controllerPlayGame);
        sceneLoader.loadSceneInNewWindow("Test Ship Placement");
    }
}
