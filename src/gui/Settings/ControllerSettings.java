package gui.Settings;

import gui.ControllerMainMenu;
import gui.WindowChange.SceneLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

public class ControllerSettings {

    @FXML
    private Button buttonBackMainMenu;



    @FXML
    public void goBackToMenu(MouseEvent event){

        ControllerMainMenu controllerMainMenu = new ControllerMainMenu();
        SceneLoader sceneLoader = new SceneLoader(buttonBackMainMenu, "../Main_Menu.fxml", controllerMainMenu);
        sceneLoader.loadSceneInExistingWindow();

    }

}
