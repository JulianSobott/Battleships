package gui.newGame;

import core.communication_data.GameSettings;
import core.communication_data.LoadGameResult;
import core.serialization.GameData;
import core.serialization.GameSerialization;
import core.utils.ResourcesDestructor;
import core.utils.logging.LoggerGUI;
import core.utils.logging.LoggerLogic;
import core.utils.logging.LoggerState;
import gui.ControllerMainMenu;
import gui.PlayGame.ControllerPlayGame;
import gui.ShipPlacement.ControllerShipPlacement;
import gui.UiClasses.Notification;
import gui.WindowChange.SceneLoader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import player.PlayerNetwork;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.UnaryOperator;

public class ControllerNewGame implements Initializable {

    private final double vBoxGameModeWidth = 0.3;
    private final double paneGameModeWidth = 0.7;

    private final double absTopHeight = 80;
    private final double centerHeight = 0.7;
    private final double botHeight = 1 - centerHeight;
    private final double absBotMarginBottom = 20;
    private final double btnGameModeWidth = 0.8;
    private final double btnGameModeHeight = 0.1;
    private final double btnGameModeWidthHover = 0.9;
    private final int MAX_INVALID_SETTINGS_MASK = 255;
    @FXML
    private Pane anchorPaneRoot;
    @FXML
    private BorderPane borderPaneRoot;
    @FXML
    private Pane paneTop;
    @FXML
    private Pane paneGameMode;
    @FXML
    private Pane paneBottom;
    @FXML
    private VBox vBoxGameMode;
    @FXML
    private Button btnGameModeAI;
    @FXML
    private Button btnGameModeNetwork;
    @FXML
    private Button btnGameModeAIvsAI;
    @FXML
    private Label lblHeader;
    @FXML
    private Button btnPlay;
    @FXML
    private TextField tfSize;
    @FXML
    private Slider sliderSize;
    private List<Button> listGameModeButtons = new ArrayList<>();
    private HashMap<Button, GameMode> hashMapGameModes = new HashMap<>();
    private GameModeControllerInterface controllerGameMode;
    private GameModeType selectedGameMode;
    private int INVALID_MODE = 1;
    private int INVALID_SIZE = 2;
    private int INVALID_GAME_MODE_SETTINGS = 4;
    private int INVALID_SETTINGS = INVALID_MODE;
    private GameData loadedGameData;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listGameModeButtons.addAll(Arrays.asList(btnGameModeAI, btnGameModeAIvsAI, btnGameModeNetwork));
        hashMapGameModes.put(btnGameModeAI,
                new GameMode("gameModeAI", "Du gegen die KI", GameModeType.AI, btnGameModeAI));
        hashMapGameModes.put(btnGameModeAIvsAI,
                new GameMode("gameModeAIvsAI", "KI gegen KI", GameModeType.AIvsAI, btnGameModeAIvsAI));
        hashMapGameModes.put(btnGameModeNetwork,
                new GameMode("gameModeNetwork", "Spiel übers Netzwerk", GameModeType.NETWORK, btnGameModeNetwork));

        // header
        paneTop.prefHeightProperty().set(absTopHeight);

        // left
        vBoxGameMode.prefWidthProperty().bind(anchorPaneRoot.widthProperty().multiply(vBoxGameModeWidth));
        vBoxGameMode.prefHeightProperty().bind(anchorPaneRoot.heightProperty().multiply(centerHeight).subtract(absTopHeight + absBotMarginBottom));

        // bottom
        paneBottom.prefHeightProperty().bind(anchorPaneRoot.heightProperty().multiply(botHeight).subtract(absTopHeight + absBotMarginBottom));
        btnPlay.prefWidthProperty().bind(paneBottom.heightProperty().subtract(absBotMarginBottom));
        btnPlay.prefHeightProperty().bind(paneBottom.heightProperty().subtract(absBotMarginBottom));

        for (Button btnGameMode : listGameModeButtons) {
            btnGameMode.prefWidthProperty().bind(vBoxGameMode.widthProperty().multiply(btnGameModeWidth));
            btnGameMode.prefHeightProperty().bind(vBoxGameMode.heightProperty().multiply(btnGameModeHeight));
            // btnGameMode hover
            btnGameMode.setOnMouseEntered(event -> {
                btnGameMode.prefWidthProperty().bind(vBoxGameMode.widthProperty().multiply(btnGameModeWidthHover));
            });
            btnGameMode.setOnMouseExited(event -> {
                btnGameMode.prefWidthProperty().bind(vBoxGameMode.widthProperty().multiply(btnGameModeWidth));
            });
            btnGameMode.setOnMouseClicked(event -> {
                setGameMode(hashMapGameModes.get(btnGameMode));
            });
        }

        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getText();
            if (text.matches("[0-9]*")) {
                return change;
            }
            return null;
        };
        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        tfSize.setTextFormatter(textFormatter);
        tfSize.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                validateTfSize();
            }
        });

        tfSize.setText(sliderSize.valueProperty().intValue() + "");
        sliderSize.valueProperty().addListener((observable, oldValue, newValue) -> {
            setPlaygroundSize(newValue.intValue(), false);
        });

        btnPlay.setOnMouseEntered(event -> {
            if (INVALID_SETTINGS == 0) {
                btnPlay.getStyleClass().add("valid");
            } else {
                btnPlay.getStyleClass().add("invalid");
            }
        });
        btnPlay.setOnMouseExited(event -> {
            btnPlay.getStyleClass().remove("valid");
            btnPlay.getStyleClass().remove("invalid");
        });
        btnPlay.setOnMouseClicked(event -> {
            if (validateSettings()) {
                GameSettings gameSettings = buildGameSettings();
                playGame(gameSettings);
            }
        });
    }

    /**
     * When a game is loaded, but this window is needed as lobby
     *
     * @param gameData Loaded GameData
     */
    public void initFromNetworkLoaded(GameData gameData) {
        loadedGameData = gameData;
        setGameMode(new GameMode("gameModeNetwork", "Spiel übers Netzwerk", GameModeType.NETWORK, btnGameModeNetwork));
        ControllerGameModeNetwork controller = (ControllerGameModeNetwork) controllerGameMode;
        controller.initFromNetworkLoaded(gameData);
        setPlaygroundSize(gameData.getCurrentPlayer().getPlaygroundOwn().getSize());
        tfSize.setDisable(true);
        sliderSize.setDisable(true);
    }

    private boolean validateSettings() {
        if (selectedGameMode == null) {
            Notification.create(btnGameModeAI)
                    .header("Kein Spielmodus ausgewählt")
                    .text("Bitte wähle zuerst einen Spielmodus auf der linken Seite aus")
                    .level(Notification.NotificationLevel.WARNING)
                    .autoHide(3000)
                    .show();
            return false;
        }
        if (!validateTfSize()) {
            return false;
        }
        return controllerGameMode.validateSettings();
    }

    private GameSettings buildGameSettings() {
        int size = (int) Double.parseDouble(tfSize.getText());
        GameSettings gameSettings = GameSettings.create().setPlaygroundSize(size);
        controllerGameMode.buildGameSettings(gameSettings);
        return gameSettings;
    }

    private void playGame(GameSettings gameSettings) {
        if (gameSettings.isFromNetworkLoad()) {
            long gameID = gameSettings.getGameID();
            if (loadedGameData == null) { //Client
                LoadGameResult res = GameSerialization.loadGame(gameID);
                if (res.getStatus() == LoadGameResult.LoadStatus.SUCCESS) {
                    this.loadedGameData = res.getGameData();
                } else {
                    Notification.create(btnGameModeAI)
                            .header("Lade Fehler")
                            .text("Das Spiel mit der ID: " + gameID + " Kann nicht geladen werden. Möglicherweise ist" +
                                    " es nicht mehr Verfügbar oder du hast es nie gespielt.")
                            .autoHide(5000)
                            .show();
                }
            } else { // Server
                gameSettings.getNetworkConnection().sendLoadGame(loadedGameData.getGameID());
                gameSettings.setGameID(loadedGameData.getGameID());
            }
            ((PlayerNetwork) loadedGameData.getPlayers()[1]).setConnected(gameSettings.getNetworkConnection());
            boolean isOtherSideStartingPlayer = loadedGameData.getCurrentPlayer() == loadedGameData.getPlayers()[1];
            LoggerLogic.debug("isOtherSideStartingPlayer=" + isOtherSideStartingPlayer);
            gameSettings.getNetworkConnection().setIsStartingPlayerOnLoad(isOtherSideStartingPlayer);
            // GameSettings are not needed anymore from here

            // load new scene: PlayGame
            LoggerGUI.info("Switch scene: NewGame --> PlayGame");
            ControllerPlayGame controller = ControllerPlayGame.fromLoad(this.loadedGameData);
            SceneLoader.loadSceneInExistingWindow(SceneLoader.GameScene.PLAY_GAME, controller);
            controller.initFieldsFromLoad(this.loadedGameData);
        } else {
            LoggerGUI.info("Switch scene: NewGame --> ShipPlacement");
            ControllerShipPlacement controllerShipPlacement = new ControllerShipPlacement(gameSettings);
            SceneLoader.loadSceneInExistingWindow(SceneLoader.GameScene.SHIP_PLACEMENT, controllerShipPlacement);
            LoggerState.info("Start Ship_Placement");
        }
    }

    private void setGameMode(GameMode gameMode) {
        removeInvalidSettings(INVALID_MODE);
        setSizeDisabled(false);
        if (gameMode.gameModeType == GameModeType.NETWORK) {
            addInvalidSettings(INVALID_GAME_MODE_SETTINGS); // Network has no default settings
        } else {
            removeInvalidSettings(INVALID_GAME_MODE_SETTINGS);
        }

        String fxmlPath = gameMode.fxmlFile;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        try {
            loader.load();
        } catch (IOException e) {
            LoggerGUI.error("Cannot load fxml: path=" + fxmlPath);
            e.printStackTrace();
        }
        controllerGameMode = loader.getController();
        controllerGameMode.linkToGodController(this);
        selectedGameMode = gameMode.gameModeType;
        Pane rootGameMode = loader.getRoot();
        rootGameMode.minWidthProperty().bind(paneGameMode.widthProperty());
        rootGameMode.maxWidthProperty().bind(paneGameMode.widthProperty());
        rootGameMode.minHeightProperty().bind(paneGameMode.heightProperty());
        rootGameMode.maxHeightProperty().bind(paneGameMode.heightProperty());
        paneGameMode.getChildren().clear();
        paneGameMode.getChildren().add(rootGameMode);

        lblHeader.setText(gameMode.headerName);
        // Highlight Button
        btnGameModeAIvsAI.getStyleClass().remove("selected");
        btnGameModeAI.getStyleClass().remove("selected");
        btnGameModeNetwork.getStyleClass().remove("selected");
        gameMode.correspondingSelectorNode.getStyleClass().add("selected");
    }

    private boolean validateTfSize() {
        if (tfSize.getText().length() == 0) {
            tfSize.getStyleClass().add("tfInvalidInput");
            addInvalidSettings(INVALID_SIZE);
            Notification.create(sliderSize)
                    .header("Ungültige Spielfeldgröße")
                    .text("Bitte eine Spielfeldgöße zwischen 5 und 30 eintragen")
                    .level(Notification.NotificationLevel.WARNING)
                    .autoHide(3000)
                    .show();
            return false;
        }
        int value = Integer.parseInt(tfSize.getText());
        if (value < 5 || value > 30) {
            tfSize.getStyleClass().add("tfInvalidInput");
            addInvalidSettings(INVALID_SIZE);
            Notification.create(sliderSize)
                    .header("Ungültige Spielfeldgröße")
                    .text("Nur Spielfeldgrößen zwischen 5 und 30 sind erlaubt")
                    .level(Notification.NotificationLevel.WARNING)
                    .autoHide(3000)
                    .show();
            return false;
        } else {
            removeInvalidSettings(INVALID_SIZE);
            setPlaygroundSize(value);
        }
        return true;
    }

    private void setPlaygroundSize(int size, boolean updateSlider) {
        assert size >= 5 && size <= 30;
        tfSize.setText(size + "");
        if (updateSlider) {
            sliderSize.setValue(size);
        }
        tfSize.getStyleClass().remove("tfInvalidInput");
    }

    private void setPlaygroundSize(int size) {
        setPlaygroundSize(size, true);
    }

    public void setValidGameModeSettings(boolean valid) {
        if (valid) {
            removeInvalidSettings(INVALID_GAME_MODE_SETTINGS);
        } else {
            addInvalidSettings(INVALID_GAME_MODE_SETTINGS);
        }
    }

    private void addInvalidSettings(int setting) {
        INVALID_SETTINGS |= setting;
    }

    private void removeInvalidSettings(int setting) {
        INVALID_SETTINGS &= MAX_INVALID_SETTINGS_MASK - setting;
    }

    public void setSizeDisabled(boolean disabled) {
        tfSize.setDisable(disabled);
        sliderSize.setDisable(disabled);
    }

    // ===================== Back to Main menu ======================
    public void backToMainMenu() {
        ResourcesDestructor.shutdownAll();
        SceneLoader.loadSceneInExistingWindow(SceneLoader.GameScene.MAIN_MENU, new ControllerMainMenu());
    }

    enum GameModeType {
        AI, AIvsAI, NETWORK
    }

    class GameMode {
        String fxmlFile;
        String headerName;
        GameModeType gameModeType;
        Button correspondingSelectorNode;

        public GameMode(String fxmlFile, String headerName, GameModeType gameModeType, Button correspondingSelectorNode) {
            this.fxmlFile = fxmlFile + ".fxml";
            this.headerName = headerName;
            this.gameModeType = gameModeType;
            this.correspondingSelectorNode = correspondingSelectorNode;
        }
    }
}
