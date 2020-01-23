package gui_tests;

import core.communication_data.GameSettings;
import gui.ControllerMainMenu;
import gui.ShipPlacement.ControllerShipPlacement;
import gui.WindowChange.SceneLoader;
import javafx.application.Application;
import javafx.stage.Stage;
import player.PlayerAI;
import player.PlayerHuman;

import static javafx.application.Application.launch;

public class TestMain extends Application{
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        int playgroundSize = 5;
        GameSettings settings = new GameSettings(playgroundSize, new PlayerHuman(0, "1", playgroundSize),
                new PlayerAI(1, "2", playgroundSize, PlayerAI.Difficulty.HARD));
        ControllerShipPlacement controller = new ControllerShipPlacement(settings);
        SceneLoader sceneLoader = new SceneLoader(null, "../../gui/ShipPlacement/ShipPlacement.fxml", controller);
        sceneLoader.loadSceneInNewWindow("Test Ship Placement");
    }
}
