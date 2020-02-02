package gui;

import core.utils.ResourcesDestructor;
import core.utils.logging.LoggerGUI;
import core.utils.logging.LoggerState;
import gui.LoadGame.ControllerLoadGame;
import gui.Media.MusicPlayer;
import gui.Settings.ControllerSettings;
import gui.WindowChange.SceneLoader;
import gui.newGame.ControllerNewGame;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerMainMenu implements Initializable {

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MusicPlayer.loadSounds();
    }

    @FXML
    void closeApplication(MouseEvent event) {
        LoggerState.info("End Program");
        ResourcesDestructor.shutdownAll();
        Stage stage = (Stage) anchorPane.getScene().getWindow();
        stage.close();
    }


    @FXML
    void createNewGame() {
        LoggerGUI.info("Switch scene: MainMenu --> NewGame");
        SceneLoader.loadSceneInExistingWindow(SceneLoader.GameScene.NEW_GAME, new ControllerNewGame());
    }


    @FXML
    void loadExistingGame(MouseEvent event) {
        LoggerGUI.info("Switch scene: MainMenu --> LoadGame");
        ControllerLoadGame controllerLoadGame = new ControllerLoadGame(this.anchorPane);
        SceneLoader.loadSceneInExistingWindowWithoutButtons(SceneLoader.GameScene.LOAD_GAME, controllerLoadGame,
                "Spiel laden");

    }

    @FXML
    void loadSettings(MouseEvent event) {
        LoggerGUI.info("Switch scene: MainMenu --> Settings");
        ControllerSettings controllerSettings = new ControllerSettings(this.anchorPane);
        SceneLoader.loadSceneInExistingWindowWithoutButtons(SceneLoader.GameScene.SETTINGS, controllerSettings,
                "Einstellungen");
    }
}
