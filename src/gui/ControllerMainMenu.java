package gui;

import gui.Settings.ControllerSettings;
import gui.WindowChange.SceneLoader;
import gui.newGame.ControllerGameType;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

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

    }

    @FXML
    void loadSettings(MouseEvent event) {

        ControllerSettings controllerSettings = new ControllerSettings();
        SceneLoader sceneLoader = new SceneLoader(this.anchorPane, "../Settings/Settingsfxml.fxml", controllerSettings);
        sceneLoader.loadSceneInExistingWindow();

    }

}
