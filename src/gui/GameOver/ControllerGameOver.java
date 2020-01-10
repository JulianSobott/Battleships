package gui.GameOver;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.effect.MotionBlur;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;



public class ControllerGameOver implements Initializable {

    @FXML
    private Label labelGameStatus;

    private final AnchorPane ANCHORPANEPLAYGROUND;


    public ControllerGameOver(AnchorPane anchorPanePlayground ,Boolean HumanWinner){

        MotionBlur motionBlur = new MotionBlur();
        this.ANCHORPANEPLAYGROUND = anchorPanePlayground;
        ANCHORPANEPLAYGROUND.setEffect(motionBlur);

    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


    }
}
