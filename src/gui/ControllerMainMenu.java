package gui;

import gui.WindowChange.LoadNewScene;
import gui.newGame.ControllerGameType;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

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
        LoadNewScene loadNewScene = new LoadNewScene(this.anchorPane, "../newGame/GameType.fxml", controllerGameType );
        loadNewScene.load();

    }


    @FXML
    void loadExistingGame(MouseEvent event) {

    }

    @FXML
    void loadSettings(MouseEvent event) {

    }

}
