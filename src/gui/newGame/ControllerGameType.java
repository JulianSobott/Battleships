package gui.newGame;

import core.Player;
import core.communication_data.GameSettings;
import core.utils.logging.LoggerGUI;
import gui.ControllerMainMenu;
import gui.ShipPlacement.ControllerShipPlacement;
import gui.WindowChange.SceneLoader;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import network.Client.Client;
import network.Connected;
import network.ConnectionStatus;
import network.Server.Server;
import network.Utils;
import player.PlayerAI;
import player.PlayerHuman;
import player.PlayerNetwork;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;


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

    @FXML
    public VBox vBoxPlaygroundSettings;

    @FXML
    public Label labelConnectionStatus;


    private static final String filepathBackMainMenu = "../Main_Menu.fxml";
    private static final String filepathShipPlacement = "../ShipPlacement/ShipPlacement.fxml";

    // Network
    private Connected networkConnection;


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

    /**
     * ...
     */

    public void btnConnectClicked() {
        if (radioButtonClient.isSelected()) {
            String ip = textFieldIpAddress.getText();
            int port = 5000;
            networkConnection= new Client(ip, port);
            ConnectionStatus status = networkConnection.start();
            LoggerGUI.info("Client connection to server: status=" + status);
            labelConnectionStatus.setText("" + status);
            // TODO: freeze GUI, open new window, ...?
        } else {
            LoggerGUI.warning("TODO: disable button connect, when server is selected");
        }
    }

    @FXML
    private void onServerSelected() {
        // TODO: only when wasn't selected before
        this.determineLocalIpAddress();
        this.startServer();
    }

    /**
     * ...
     */

    @FXML
    private void onClientSelected() {
        // TODO: only when wasn't selected before
        this.setClientInformation();
        this.stopServer();
    }

    /**
     * ...
     */

    private void startServer() {
        LoggerGUI.info("Starting server");
        Server server = new Server(5000);
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                server.start();
                server.waitTillClientConnected();
                return null;
            }
        };
        task.setOnSucceeded(workerStateEvent -> labelConnectionStatus.setText("Client connected"));
        new Thread(task).start();
    }

    /**
     * ...
     */

    private void stopServer() {
        LoggerGUI.info("Stopping server");
        if (networkConnection != null && networkConnection.isStarted()) {
            networkConnection.stopListening();
        }
    }

    /**
     * ...
     */

    @FXML
    private void determineLocalIpAddress() {
        textFieldIpAddress.setText(Utils.getIpAddress());
    }

    /**
     * ...
     */

    @FXML
    private void setClientInformation() {

        textFieldIpAddress.clear();
    }


    /**
     * ...
     */

    @FXML
    private void accentuateSettingsForCurrentGamType() {

        if (radioButtonKI.isSelected()) {

            vBoxKI.setStyle("-fx-background-color: #626D71");
            vBoxNetzwerk.setStyle("-fx-background-color: lightgray");
            vBoxLocal.setStyle("-fx-background-color: lightgray");
            vBoxPlaygroundSettings.setStyle("-fx-background-color: #626D71");
        } else if (radioButtonNetzwerk.isSelected()){

            vBoxKI.setStyle("-fx-background-color: lightgray");
            vBoxNetzwerk.setStyle("-fx-background-color: #626D71");
            vBoxLocal.setStyle("-fx-background-color: lightgray");
            vBoxPlaygroundSettings.setStyle("-fx-background-color: #626D71");
        } else if (radioButtonLocal.isSelected()){

            vBoxKI.setStyle("-fx-background-color: lightgray");
            vBoxNetzwerk.setStyle("-fx-background-color: lightgray");
            vBoxLocal.setStyle("-fx-background-color: #626D71");
            vBoxPlaygroundSettings.setStyle("-fx-background-color: #626D71");
        }
    }


    /**
     * ##########################################   generate Settings  ###############################################
     */


    /**
     * ...
     */

    private GameSettings buildGameSettings() {
        // TODO: Surface validation
        // TODO: get playgroundSize from Server
        // int playgroundSize = client.getPlaygroundSize();
        int playgroundSize = Integer.parseInt(this.textfieldPlaygroundSize.getText());
        Player p1 = new PlayerHuman(0, "TODO", playgroundSize);
        Player p2;
        if (this.radioButtonKI.isSelected() && this.radioButtonEasy.isSelected()) {
            p2 = new PlayerAI(1, "KI", playgroundSize);
        } else if (this.radioButtonNetzwerk.isSelected()) {
            // TODO: Set player in Connected or add connected to player
            p2 = new PlayerNetwork(1, "KI", playgroundSize, null);
        } else {
            p2 = new PlayerHuman(1, "KI", playgroundSize);
        }
        return new GameSettings(playgroundSize, p1, p2);
    }


    /**
     * ##########################################   Window Navigation  ##############################################
     */


    /**
     * ...
     */

    @FXML
    public void goBacktoMainMenus(MouseEvent event) {

        ControllerMainMenu controllerMainMenu = new ControllerMainMenu();
        SceneLoader sceneLoader = new SceneLoader(BackToMenu, filepathBackMainMenu, controllerMainMenu);
        sceneLoader.loadSceneInExistingWindow();


    }

    /**
     * ...
     */

    @FXML
    public void loadShipPöacementScene() {
        GameSettings settings = buildGameSettings();

        ControllerShipPlacement controllerShipPlacement = new ControllerShipPlacement(settings);
        SceneLoader sceneLoader = new SceneLoader(BackToMenu, filepathShipPlacement, controllerShipPlacement);
        sceneLoader.loadSceneInExistingWindow();

    }

}






