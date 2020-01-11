package gui.GameOver;

import gui.ControllerMainMenu;
import gui.WindowChange.SceneLoader;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.effect.MotionBlur;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;



public class ControllerGameOver implements Initializable {

    @FXML
    private Label labelGameStatus;

    @FXML
    private VBox vBoxColorResult;

    private final AnchorPane ANCHORPANEPLAYGROUND;

    private final String FILEPATHMAINMENU = "../Main_Menu.fxml";
    private Boolean humanPlayerWins;


    public ControllerGameOver(AnchorPane anchorPanePlayground ,Boolean humanWinner){

        MotionBlur motionBlur = new MotionBlur();
        this.ANCHORPANEPLAYGROUND = anchorPanePlayground;
        ANCHORPANEPLAYGROUND.setEffect(motionBlur);
        this.humanPlayerWins = humanWinner;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        if(humanPlayerWins)
        {
            labelGameStatus.setText("You Win");
            vBoxColorResult.setStyle("-fx-background-color: green");
        }
        else {
            labelGameStatus.setText("You lost");
            vBoxColorResult.setStyle("-fx-background-color: red");
        }
    }


    public void goBackToMainMenu(){

        ANCHORPANEPLAYGROUND.setEffect(null);

        Stage stage = (Stage) labelGameStatus.getScene().getWindow();
        stage.close();

        ControllerMainMenu controllerMainMenu = new ControllerMainMenu();
        SceneLoader sceneLoader = new SceneLoader(ANCHORPANEPLAYGROUND, FILEPATHMAINMENU, controllerMainMenu);
        sceneLoader.loadSceneInExistingWindow();


    }

}
