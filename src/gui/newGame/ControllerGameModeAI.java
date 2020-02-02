package gui.newGame;

import core.communication_data.GameSettings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import player.PlayerAI;
import player.PlayerHuman;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerGameModeAI implements Initializable, GameModeControllerInterface {

    @FXML
    private AnchorPane rootAnchorPane;
    @FXML
    private VBox vBoxRoot;
    @FXML
    private CheckBox cbSlowAIShooting;
    @FXML
    private CheckBox cbCheatMode;
    @FXML
    private RadioButton rbDifficultyEasy;
    @FXML
    private RadioButton rbDifficultyMedium;
    @FXML
    private RadioButton rbDifficultyHard;

    private ControllerNewGame controllerNewGame;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //        vBoxRoot.prefWidthProperty().bind(rootAnchorPane.widthProperty());
        vBoxRoot.translateXProperty().bind(rootAnchorPane.widthProperty().divide(2).subtract(vBoxRoot.widthProperty().divide(2)));
        vBoxRoot.translateYProperty().bind(rootAnchorPane.heightProperty().divide(2).subtract(vBoxRoot.heightProperty().divide(2)));
    }

    @Override
    public void linkToGodController(ControllerNewGame controllerNewGame) {
        this.controllerNewGame = controllerNewGame;
    }

    @Override
    public boolean validateSettings() {
        return true;
    }

    @Override
    public void buildGameSettings(GameSettings gameSettings) {
        boolean slowAIShooting = cbSlowAIShooting.isSelected();
        boolean showHeatMap = cbCheatMode.isSelected();
        PlayerHuman p1 = new PlayerHuman(0, "Human", gameSettings.getPlaygroundSize());
        PlayerAI.Difficulty difficulty =
                rbDifficultyEasy.isSelected() ? PlayerAI.Difficulty.EASY :
                        rbDifficultyMedium.isSelected() ? PlayerAI.Difficulty.MEDIUM :
                                PlayerAI.Difficulty.HARD;
        PlayerAI p2 = new PlayerAI(1, "AI", gameSettings.getPlaygroundSize(), difficulty);
        gameSettings.setSlowAiShooting(slowAIShooting).setShowHeatMap(showHeatMap).setP1(p1).setP2(p2).setStartingPlayer(p1);
    }
}
