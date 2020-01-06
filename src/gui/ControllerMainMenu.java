package gui;

import core.GameManager;
import core.communication_data.GameSettings;
import core.communication_data.LoadGameResult;
import core.serialization.GameData;
import core.serialization.GameSerialization;
import gui.LoadGame.ControllerLoadGame;
import gui.PlayGame.ControllerPlayGame;
import gui.Settings.ControllerSettings;
import gui.ShipPlacement.ControllerShipPlacement;
import gui.WindowChange.SceneLoader;
import gui.newGame.ControllerGameType;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import player.PlayerAI;
import player.PlayerHuman;

public class ControllerMainMenu {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Button buttonNewGame;

    @FXML
    private Button buttonLoadGame;

    @FXML
    private Button buttonSettings;

    @FXML
    private Button buttonExit;

    @FXML
    void closeApplication(MouseEvent event) {

        Stage stage = (Stage) anchorPane.getScene().getWindow();
        stage.close();
    }


    @FXML
    void createNewGame() {

        ControllerGameType controllerGameType = new ControllerGameType();
        SceneLoader sceneLoader = new SceneLoader(this.anchorPane, "../newGame/GameType.fxml", controllerGameType);
        sceneLoader.loadSceneInExistingWindow();
    }


    @FXML
    void loadExistingGame(MouseEvent event) {
         // TODO: open dialog with all saved games
        ControllerLoadGame controllerLoadGame = new ControllerLoadGame();
        SceneLoader sceneLoader = new SceneLoader(null, "../LoadGame/LoadGame.fxml", controllerLoadGame);
        sceneLoader.loadSceneInExistingWindowWithoutButtons("Load Game");


        // Hardcoded for debug purposes
        LoadGameResult res = GameSerialization.loadGame(1);
        if (res.getStatus() == LoadGameResult.LoadStatus.SUCCESS) {
            GameData gameData = res.getGameData();
            ControllerPlayGame controller = ControllerPlayGame.fromLoad(gameData);
          //  SceneLoader sceneLoader = new SceneLoader(this.anchorPane, "../PlayGame/PlayGame.fxml", controller);
          // sceneLoader.loadSceneInExistingWindow();
        } else {
            // TODO: inform user
        }

    }

    @FXML
    void loadSettings(MouseEvent event) {

        ControllerSettings controllerSettings = new ControllerSettings();
        SceneLoader sceneLoader = new SceneLoader(this.anchorPane, "../Settings/Settingsfxml.fxml", controllerSettings);
        sceneLoader.loadSceneInExistingWindow();

    }

}
