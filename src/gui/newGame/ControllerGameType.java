package gui.newGame;

import core.GameManager;
import core.Player;
import core.communication_data.GameSettings;
import core.communication_data.NewGameResult;
import gui.ControllerMainMenu;
import gui.ShipPlacement.ControllerShipPlacement;
import gui.WindowChange.SceneLoader;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import player.PlayerAI;
import player.PlayerHuman;
import player.PlayerNetwork;

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
    public RadioButton radioButtonServer;

    @FXML
    public RadioButton radioButtonClient;

    @FXML
    public RadioButton radioButtonEasy;

    @FXML
    public RadioButton radioButtonMedium;

    @FXML
    public  RadioButton radioButtonHard;

    @FXML
    public TextField textfieldPlaygroundSize;


    private static final String filepathBackMainMenu = "../Main_Menu.fxml";
    private static final String filepathShipPlacement = "../ShipPlacement/ShipPlacement.fxml";


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        ToggleGroup toggleGroupGameType = new ToggleGroup();
        radioButtonKI.setToggleGroup(toggleGroupGameType);
        radioButtonNetzwerk.setToggleGroup(toggleGroupGameType);
        radioButtonLocal.setToggleGroup(toggleGroupGameType);

        ToggleGroup toggleGroupServerClient = new ToggleGroup();
        radioButtonClient.setToggleGroup(toggleGroupServerClient);
        radioButtonServer.setToggleGroup(toggleGroupServerClient);

        ToggleGroup toggleGroupDifficulty = new ToggleGroup();
        radioButtonEasy.setToggleGroup(toggleGroupDifficulty);
        radioButtonMedium.setToggleGroup(toggleGroupDifficulty);
        radioButtonHard.setToggleGroup(toggleGroupDifficulty);


    }


    @FXML
    public void goBacktoMainMenus (MouseEvent event){

        ControllerMainMenu controllerMainMenu = new ControllerMainMenu();
        SceneLoader sceneLoader = new SceneLoader(BackToMenu, filepathBackMainMenu, controllerMainMenu);
        sceneLoader.loadSceneInExistingWindow();


    }

    @FXML
    public void loadShipPöacementScene(){
        GameSettings settings = buildGameSettings();

        ControllerShipPlacement controllerShipPlacement = new ControllerShipPlacement(settings);
        SceneLoader sceneLoader = new SceneLoader(BackToMenu, filepathShipPlacement, controllerShipPlacement);
        sceneLoader.loadSceneInExistingWindow();

    }


    private GameSettings buildGameSettings() {
        // TODO: Surface validation
        int playgroundSize = Integer.parseInt(this.textfieldPlaygroundSize.getText());
        Player p1 = new PlayerHuman("TODO", playgroundSize);
        Player p2;
        if(this.radioButtonKI.isSelected()){
            p2 = new PlayerAI("KI", playgroundSize);
        }else if(this.radioButtonNetzwerk.isSelected()){
            p2 = new PlayerNetwork("KI", playgroundSize);
        }else{
            p2 = new PlayerHuman("KI", playgroundSize);
        }
        return new GameSettings(playgroundSize, p1, p2);
    }
}






