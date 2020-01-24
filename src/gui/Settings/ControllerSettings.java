package gui.Settings;


import core.Player;
import core.communication_data.LoadGameResult;
import core.serialization.GameData;
import core.serialization.GameSerialization;
import core.utils.logging.LoggerGUI;
import gui.PlayGame.ControllerPlayGame;
import gui.WindowChange.SceneLoader;
import gui.newGame.ControllerGameType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.effect.MotionBlur;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import player.PlayerNetwork;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ControllerSettings implements Initializable {

    @FXML
    private CheckBox checkboxEnableMusicClick;

    @FXML
    private Slider sliderMusicVolume;

    @FXML
    AnchorPane anchorPaneSettings;

    private final AnchorPane ANCHORPANE_MAINMENU;


    public ControllerSettings(AnchorPane anchorPaneMainMenu) {

        MotionBlur motionBlur = new MotionBlur();
        this.ANCHORPANE_MAINMENU = anchorPaneMainMenu;
        ANCHORPANE_MAINMENU.setEffect(motionBlur);
    }


    /**
     *
     */

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    /**
     * closes the window in which the scores are selected
     */
    public void closeWindow() {
        ANCHORPANE_MAINMENU.setEffect(null);
        Stage stage = (Stage) anchorPaneSettings.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void onCheckboxEnableMusicClick() {
        LoggerGUI.debug("onCheckboxEnableMusicClick");
    }

}
