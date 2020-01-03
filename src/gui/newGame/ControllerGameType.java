package gui.newGame;

import core.GameManager;
import core.Player;
import core.communication_data.GameSettings;
import core.communication_data.NewGameResult;
import core.utils.logging.LoggerGUI;
import gui.ControllerMainMenu;
import gui.ShipPlacement.ControllerShipPlacement;
import gui.WindowChange.SceneLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import player.PlayerAI;
import player.PlayerHuman;
import player.PlayerNetwork;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;


public class ControllerGameType implements Initializable {

    @FXML
    public Button BackToMenu;

    @FXML
    public RadioButton radioButtonKI;

    @FXML
    public RadioButton radioButtonNetzwerk;

    @FXML
    public RadioButton radioButtonLocal;

    @FXML
    public RadioButton radioButtonServer;

    @FXML
    public RadioButton radioButtonClient;

    @FXML
    public RadioButton radioButtonEasy;

    @FXML
    public RadioButton radioButtonMedium;

    @FXML
    public RadioButton radioButtonHard;

    @FXML
    public TextField textfieldPlaygroundSize;

    @FXML
    public TextField textFieldIpAddress;

    @FXML
    public VBox vBoxKI;

    @FXML
    public VBox vBoxNetzwerk;

    @FXML
    public VBox vBoxLocal;


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


    /**
     * #################################################   JavaFX Events  ############################################
     */

    @FXML
    private void onServerSelected() {
        // TODO: only when wasn't selected before
        this.determineLocalIpAddress();
        this.startServer();
    }

    @FXML
    private void onClientSelected() {
        // TODO: only when wasn't selected before
        this.setClientInformation();
        this.stopServer();
    }

    private void startServer() {
        LoggerGUI.info("Starting server");
        // TODO
    }

    private void stopServer() {
        LoggerGUI.info("Stopping server");
        // TODO
    }

    @FXML
    private void determineLocalIpAddress() {
        // TODO: move parts to network package
        InetAddress localIp = null;
        try {
            localIp = Inet4Address.getLocalHost();

        } catch (UnknownHostException exception) {
            //TODO Loggen
        }
        if (localIp != null) {
            String[] s = localIp.toString().split("/");
            textFieldIpAddress.setText(s[1]);
        } else {
            //TODO logger
        }
    }

    @FXML
    private void setClientInformation() {

        textFieldIpAddress.clear();
    }


    @FXML
    private void accentuateSettingsForCurrentGamType() {

        if (radioButtonKI.isSelected()) {

            vBoxKI.setStyle("-fx-background-color: #626D71");
            vBoxNetzwerk.setStyle("-fx-background-color: lightgray");
            vBoxLocal.setStyle("-fx-background-color: lightgray");
        } else if (radioButtonNetzwerk.isSelected()){

            vBoxKI.setStyle("-fx-background-color: lightgray");
            vBoxNetzwerk.setStyle("-fx-background-color: #626D71");
            vBoxLocal.setStyle("-fx-background-color: lightgray");

        } else if (radioButtonLocal.isSelected()){

            vBoxKI.setStyle("-fx-background-color: lightgray");
            vBoxNetzwerk.setStyle("-fx-background-color: lightgray");
            vBoxLocal.setStyle("-fx-background-color: #626D71");
        }
    }


    /**
     * ##########################################   generate Settings  ###############################################
     */


    private GameSettings buildGameSettings() {
        // TODO: Surface validation
        int playgroundSize = Integer.parseInt(this.textfieldPlaygroundSize.getText());
        Player p1 = new PlayerHuman(0, "TODO", playgroundSize);
        Player p2;
        if (this.radioButtonKI.isSelected() && this.radioButtonEasy.isSelected()) {
            p2 = new PlayerAI(1, "KI", playgroundSize);
        } else if (this.radioButtonNetzwerk.isSelected()) {
            p2 = new PlayerNetwork(1, "KI", playgroundSize);
        } else {
            p2 = new PlayerHuman(1, "KI", playgroundSize);
        }
        return new GameSettings(playgroundSize, p1, p2);
    }


    /**
     * ##########################################   Window Navigation  ##############################################
     */

    @FXML
    public void goBacktoMainMenus(MouseEvent event) {

        ControllerMainMenu controllerMainMenu = new ControllerMainMenu();
        SceneLoader sceneLoader = new SceneLoader(BackToMenu, filepathBackMainMenu, controllerMainMenu);
        sceneLoader.loadSceneInExistingWindow();


    }

    @FXML
    public void loadShipPöacementScene() {
        GameSettings settings = buildGameSettings();

        ControllerShipPlacement controllerShipPlacement = new ControllerShipPlacement(settings);
        SceneLoader sceneLoader = new SceneLoader(BackToMenu, filepathShipPlacement, controllerShipPlacement);
        sceneLoader.loadSceneInExistingWindow();

    }

}






