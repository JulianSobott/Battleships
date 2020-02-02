package gui.Settings;


import core.utils.logging.LoggerGUI;
import gui.Media.MusicPlayer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.effect.MotionBlur;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerSettings implements Initializable {

    @FXML
    private CheckBox checkboxEnableMusic;

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
        sliderMusicVolume.valueProperty().addListener((observableValue, number, newValue) -> setMusicVolume(newValue.intValue()));
    }

    /**
     * closes the window in which the scores are selected
     */
    public void closeWindow() {
        LoggerGUI.info("Switch scene: Settings --> MainMenu");
        ANCHORPANE_MAINMENU.setEffect(null);
        Stage stage = (Stage) anchorPaneSettings.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void onCheckboxEnableMusicClick() {
        if(checkboxEnableMusic.isSelected()) {
            MusicPlayer.startMusic();
            sliderMusicVolume.setValue(MusicPlayer.getVolume() * 100);
        } else {
            MusicPlayer.stopMusic();
        }
    }

    public void setMusicVolume(int newValue) {
        MusicPlayer.setVolume((double)newValue/100);
    }

}
