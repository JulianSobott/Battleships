package gui.newGame;

import gui.ControllerMainMenu;
import gui.WindowChange.SceneLoader;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;


public class ControllerGameType implements Initializable {

    @FXML
    public Button BackToMenu;

    @FXML
    public RadioButton radioButtonKI;

    @FXML
    public RadioButton radioButtonNetzwerk;

    @FXML
    public  RadioButton radioButtonLocal;



    @FXML
    public void goBacktoMainMenus (MouseEvent event){

        ControllerMainMenu controllerMainMenu = new ControllerMainMenu();
        SceneLoader sceneLoader = new SceneLoader(BackToMenu, "../Main_Menu.fxml", controllerMainMenu);
        sceneLoader.loadSceneInExistingWindow();


    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        ToggleGroup toggleGroupGameType = new ToggleGroup();
        radioButtonKI.setToggleGroup(toggleGroupGameType);
        radioButtonNetzwerk.setToggleGroup(toggleGroupGameType);
        radioButtonLocal.setToggleGroup(toggleGroupGameType);



    }
}




