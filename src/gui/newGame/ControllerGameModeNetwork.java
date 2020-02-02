package gui.newGame;

import core.Player;
import core.communication_data.GameSettings;
import core.serialization.GameData;
import core.utils.ResourcesDestructor;
import core.utils.logging.LoggerGUI;
import gui.UiClasses.Notification;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import network.*;
import player.PlayerAI;
import player.PlayerHuman;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerGameModeNetwork implements Initializable, GameModeControllerInterface {

    @FXML
    private AnchorPane rootAnchorPane;
    @FXML
    private VBox vBoxRoot;
    @FXML
    private Label lblConnectionStatus;
    @FXML
    private CheckBox cbAINetwork;
    @FXML
    private TextField tfIPAddress;
    @FXML
    private ToggleGroup toggleGroupServerClient;
    @FXML
    private RadioButton rbServer;
    @FXML
    private RadioButton rbClient;
    @FXML
    private CheckBox cbCheatMode;
    @FXML
    private Button btnConnect;
    @FXML
    private ProgressIndicator progressIndicatorConnect;
    @FXML
    private Button btnCancelConnect;

    private ControllerNewGame controllerNewGame;
    private Connected networkConnection;

    private boolean fromNetworkLoad;

    private Thread connectingThread;
    private Task<ConnectionStatus> connectingTask;


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
        if (!rbClient.isSelected() && !rbServer.isSelected()) {
            Notification.create(rbClient)
                    .text("Du musst auswählen, ob du Client oder Server sein willst")
                    .level(Notification.NotificationLevel.WARNING)
                    .autoHide(3000)
                    .show();
            return false;
        }
        if (networkConnection == null || !networkConnection.isConnected()) {
            Notification.create(rbClient)
                    .header("Verbindungsfehler")
                    .text("Du musst dich erst mit deinem Mitspieler verbinden")
                    .level(Notification.NotificationLevel.WARNING)
                    .autoHide(3000)
                    .show();
            return false;
        }
        return true;
    }

    @Override
    public void buildGameSettings(GameSettings gameSettings) {
        long gameID = -1;
        boolean p2IsStarting = false;
        Player p1;
        Player p2;

        networkConnection.startCommunication();
        // Client
        if (rbClient.isSelected()) {
            boolean loadGame = networkConnection.isLoadGame();
            if (loadGame) {
                fromNetworkLoad = true;
                gameSettings.setGameID(networkConnection.getGameID());
            } else {
                gameSettings.setPlaygroundSize(networkConnection.getPlaygroundSize());
            }
        }
        // Server
        else if (rbServer.isSelected()) {
            if (!fromNetworkLoad) {
                ((Server) networkConnection).startGame(gameSettings.getPlaygroundSize());
            }
            p2IsStarting = true;
        }
        if (!fromNetworkLoad) {
            p2 = networkConnection.getPlayerNetwork();
        } else {
            p2 = null; // Will be set to loaded Player later
        }
        if (cbAINetwork.isSelected()) {
            p1 = new PlayerAI(0, "AI_0", gameSettings.getPlaygroundSize(), PlayerAI.Difficulty.HARD);
            gameSettings.setAiVsAi(true);
        } else {
            p1 = new PlayerHuman(0, "Human", gameSettings.getPlaygroundSize());
        }
        if (cbCheatMode.isSelected()) {
            gameSettings.setShowHeatMap(true);
        }
        gameSettings.setP1(p1).setP2(p2).setStartingPlayer(p2IsStarting ? p2 : p1).setNetworkConnection(networkConnection)
                .setFromNetworkLoad(fromNetworkLoad);
    }

    public void initFromNetworkLoaded(GameData gameData) {
        fromNetworkLoad = true;
        rbClient.setDisable(true);
        rbServer.setSelected(true);
        onServerSelected();
        cbCheatMode.setDisable(true);
        cbAINetwork.setSelected(gameData.getPlayers()[0] instanceof PlayerAI);
        cbAINetwork.setDisable(true);
    }

    public void btnConnectClicked() {
        String ip = tfIPAddress.getText();
        if (ip.length() == 0) {
            Notification.create(btnConnect)
                    .header("Invalide IP-Addresse")
                    .text("Gib die IP Adresse an, die bei deinem Mitspieler angezeigt wird")
                    .level(Notification.NotificationLevel.WARNING)
                    .autoHide(4000)
                    .show();
            return;
        }
        progressIndicatorConnect.setVisible(true);
        btnCancelConnect.setVisible(true);
        btnConnect.setDisable(true);
        connectingTask = new Task<ConnectionStatus>() {
            @Override
            protected ConnectionStatus call() throws Exception {
                int port = 50000;
                networkConnection = new Client(ip, port);
                return networkConnection.start();
            }
        };
        connectingTask.setOnSucceeded(event -> {
            ConnectionStatus status = connectingTask.getValue();
            if (status == ConnectionStatus.SUCCESSFUL) {
                controllerNewGame.setValidGameModeSettings(true);
                lblConnectionStatus.getStyleClass().remove("lblFail");
                lblConnectionStatus.getStyleClass().add("lblSuccess");
            } else {
                controllerNewGame.setValidGameModeSettings(false);
                lblConnectionStatus.getStyleClass().remove("lblSuccess");
                lblConnectionStatus.getStyleClass().add("lblFail");
            }
            LoggerGUI.info("Client connection to server: status=" + status);
            lblConnectionStatus.setText("" + status);
            progressIndicatorConnect.setVisible(false);
            btnCancelConnect.setVisible(false);
            btnConnect.setDisable(false);

            ResourcesDestructor.stopSingleThread(connectingThread);
        });
        connectingTask.setOnCancelled(event -> {
            LoggerGUI.info("Connecting to server cancelled");
            progressIndicatorConnect.setVisible(false);
            btnCancelConnect.setVisible(false);
            btnConnect.setDisable(false);

            lblConnectionStatus.setText("Nicht verbunden");
            ResourcesDestructor.stopSingleThread(connectingThread);
        });
        connectingThread = new Thread(connectingTask);
        connectingThread.setName("GUI_ClientConnect");
        ResourcesDestructor.addThread(connectingThread);
        connectingThread.start();
    }

    public void btnCancelConnectClicked() {
        if (connectingTask.isRunning()) {
            connectingTask.cancel();
        }
    }

    @FXML
    private void onServerSelected() {
        lblConnectionStatus.setText("Nicht verbunden");
        btnConnect.setVisible(false);
        tfIPAddress.setEditable(false);
        this.determineLocalIpAddress();
        this.startServer();
        controllerNewGame.setSizeDisabled(false);
    }

    /**
     * ...
     */

    @FXML
    private void onClientSelected() {
        lblConnectionStatus.setText("Nicht verbunden");
        btnConnect.setVisible(true);
        tfIPAddress.setEditable(true);
        this.stopServer();
        controllerNewGame.setSizeDisabled(true);
    }

    /**
     * ...
     */

    private void startServer() {
        LoggerGUI.info("Starting server");
        Server server = new Server(50000);
        ResourcesDestructor.addServer(server);
        networkConnection = server;
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                server.start();
                server.waitTillClientConnected();
                return null;
            }
        };
        Thread thread = new Thread(task);
        task.setOnSucceeded(workerStateEvent -> {
            lblConnectionStatus.setText("Spieler verbunden");
            lblConnectionStatus.getStyleClass().add("lblSuccess");
            ResourcesDestructor.stopSingleThread(thread);
            controllerNewGame.setValidGameModeSettings(true);
        });
        thread.setName("GUI_ServerWaitForClient");
        thread.start();
        ResourcesDestructor.addThread(thread);
    }

    /**
     * ...
     */

    private void stopServer() {
        if (networkConnection instanceof Server && networkConnection.isStarted()) {
            LoggerGUI.info("Stopping server");
            ResourcesDestructor.shutdownServer();
        }
    }

    /**
     * ...
     */

    @FXML
    private void determineLocalIpAddress() {
        tfIPAddress.setText(Utils.getIpAddress());
    }

}
