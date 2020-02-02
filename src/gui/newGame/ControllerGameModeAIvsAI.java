package gui.newGame;

import core.communication_data.GameSettings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import player.PlayerAI;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class ControllerGameModeAIvsAI implements Initializable, GameModeControllerInterface {

    @FXML
    private AnchorPane rootAnchorPane;
    @FXML
    private VBox vBoxRoot;
    @FXML
    private ChoiceBox<String> choiceBoxDifficultyAI1;
    @FXML
    private ChoiceBox<String> choiceBoxDifficultyAI2;

    private ControllerNewGame controllerNewGame;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
        HashMap<String, PlayerAI.Difficulty> hashMap = new HashMap<String, PlayerAI.Difficulty>() {{
            put("Leicht", PlayerAI.Difficulty.EASY);
            put("Mittel", PlayerAI.Difficulty.MEDIUM);
            put("Schwer", PlayerAI.Difficulty.HARD);
        }};
        PlayerAI.Difficulty difficulty1 = hashMap.get(choiceBoxDifficultyAI1.getValue());
        PlayerAI.Difficulty difficulty2 = hashMap.get(choiceBoxDifficultyAI2.getValue());

        PlayerAI p1 = new PlayerAI(0, "AI0", gameSettings.getPlaygroundSize(), difficulty1);
        PlayerAI p2 = new PlayerAI(1, "AI1", gameSettings.getPlaygroundSize(), difficulty2);
        gameSettings.setP1(p1).setP2(p2).setStartingPlayer(p1).setAiVsAi(true);
    }
}
