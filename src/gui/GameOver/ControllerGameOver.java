package gui.GameOver;

import core.utils.logging.LoggerGUI;
import gui.ControllerMainMenu;
import gui.Media.MusicPlayer;
import gui.WindowChange.SceneLoader;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
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
    @FXML
    private  Label labelRounds;
    @FXML
    private ProgressBar progressbarHits;
    @FXML
    private ProgressBar progressbarMisses;

    private final AnchorPane ANCHORPANEPLAYGROUND;

    private final String FILEPATHMAINMENU = "../Main_Menu.fxml";
    private Boolean humanPlayerWins;
    private int numRounds;
    private int numHits;
    private int numMisses;


    public ControllerGameOver(AnchorPane anchorPanePlayground ,Boolean humanWinner, int numRounds, int numHits,
                              int numMisses){

        MotionBlur motionBlur = new MotionBlur();
        this.ANCHORPANEPLAYGROUND = anchorPanePlayground;
        ANCHORPANEPLAYGROUND.setEffect(motionBlur);
        this.humanPlayerWins = humanWinner;
        if(humanWinner) {
            MusicPlayer.playSound(MusicPlayer.Sound.WIN);
        } else {
            MusicPlayer.playSound(MusicPlayer.Sound.LOSE);
        }
        this.numRounds = numRounds;
        this.numHits = numHits;
        this.numMisses = numMisses;
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

        labelRounds.setText(numRounds + "");
        progressbarHits.setProgress((double)numHits/(numHits + numMisses));
        progressbarMisses.setProgress((double)numMisses/(numHits + numMisses));
    }


    public void goBackToMainMenu(){
        LoggerGUI.info("Switch scene: EndScreen --> MainMenu");
        ANCHORPANEPLAYGROUND.setEffect(null);

        Stage stage = (Stage) labelGameStatus.getScene().getWindow();
        stage.close();

        ControllerMainMenu controllerMainMenu = new ControllerMainMenu();
        SceneLoader sceneLoader = new SceneLoader(ANCHORPANEPLAYGROUND, FILEPATHMAINMENU, controllerMainMenu);
        sceneLoader.loadSceneInExistingWindow();


    }

}
