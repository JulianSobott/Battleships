package gui;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

    // Todo  Fehler finden: Event wird est beim zweiten mal gefeuert?? Warum

    @FXML
    void createNewGame(Event event) {

      //  button1.setOnAction(e -> primaryStage.setScene(scene2));
        buttonNewGame.setOnAction( e -> {

            Parent fxmlFile = null;
            try {
                fxmlFile = FXMLLoader.load(getClass().getResource("../gui/newGame/GameType.fxml"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            Stage stage = (Stage) anchorPane.getScene().getWindow();
            stage.setScene(new Scene(fxmlFile));
        });

    }

    @FXML
    void loadExistingGame(MouseEvent event) {

    }

    @FXML
    void loadSettings(MouseEvent event) {

    }

}
