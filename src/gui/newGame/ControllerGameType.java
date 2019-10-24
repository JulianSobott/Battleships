package gui.newGame;

import gui.ControllerMainMenu;
import gui.WindowChange.SceneLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;


public class ControllerGameType {

    @FXML
    public Button BackToMenu;



    @FXML
    public void goBacktoMainMenus (MouseEvent event){

        ControllerMainMenu controllerMainMenu = new ControllerMainMenu();
        SceneLoader sceneLoader = new SceneLoader(BackToMenu, "../Main_Menu.fxml", controllerMainMenu);
        sceneLoader.loadSceneInExistingWindow();


    }


}




