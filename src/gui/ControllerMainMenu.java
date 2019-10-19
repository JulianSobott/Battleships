package gui;

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

        Parent window = null;

        try {
            window = FXMLLoader.load(getClass().getResource("newGame/GameType.fxml"));

            Stage stageNewGame = (Stage) anchorPane.getScene().getWindow();
            stageNewGame.setScene(new Scene(window));
        } catch (IOException ex) {

        }
    }


    @FXML
    void loadExistingGame(MouseEvent event) {

    }

    @FXML
    void loadSettings(MouseEvent event) {

    }

}
