package gui.PlayGame;

import core.GameManager;
import core.Player;
import core.Ship;
import core.communication_data.*;
import core.playgrounds.Playground;
import core.playgrounds.PlaygroundInterface;
import core.serialization.GameData;
import core.utils.ResourcesDestructor;
import core.utils.logging.LoggerGUI;
import gui.ControllerMainMenu;
import gui.GameOver.ControllerGameOver;
import gui.Media.MusicPlayer;
import gui.UiClasses.BattleShipGui;
import gui.UiClasses.Notification;
import gui.UiClasses.PaneExtends;
import gui.WindowChange.SceneLoader;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import player.PlayerAI;
import player.PlaygroundHeatmap;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

public class ControllerPlayGame implements Initializable, InGameGUI {

    @FXML
    public GridPane gridPaneOwnField;
    @FXML
    public Label labelOwnPlayground;

    @FXML
    public GridPane gridPaneEnemyField;
    @FXML
    public Label labelEnemyPlayground;

    @FXML
    public HBox hBoxPlaygrounds;

    @FXML
    public VBox vBoxOwnPlayground;

    @FXML
    public VBox vBoxEnemyPlayground;

    @FXML
    public Button buttonBack;

    @FXML
    public AnchorPane anchorPanePlayGame;


    private double CELL_PERCENTAGE_WIDTH;
    private int playgroundSize;

    private PlaygroundHeatmap playgroundHeatmap;
    private boolean showHeatMap = false;
    private boolean slowAIShooting = false;
    private long AI_SHOOTING_DELAY_MS = 800;
    private boolean loadEndScreen = true;
    private boolean aiVsAi = false;

    ArrayList<BattleShipGui> shipPositionList;
    GameManager gameManager;
    private Cursor defaultCursor;

    // 0: Player1, 1: Player2
    private HashMap<Integer, GridPane> playerGridPaneHashMap = new HashMap<>();
    private Thread playgroundUpdaterThread;

    public ControllerPlayGame(int playgroudSize, ArrayList<BattleShipGui> shipPositionList, GameManager gameManager,
                              GameSettings gameSettings) {
        this.playgroundSize = playgroudSize;
        this.playgroundHeatmap = new PlaygroundHeatmap(playgroudSize);
        this.shipPositionList = shipPositionList;
        this.gameManager = gameManager;

        this.slowAIShooting = gameSettings.isSlowAiShooting();
        this.showHeatMap = gameSettings.isShowHeatMap();
        this.aiVsAi = gameSettings.isAiVsAi();
    }

    /**
     * Factory method when game was loaded
     *
     * @param gameData from loading
     * @return A new Controller
     */
    public static ControllerPlayGame fromLoad(GameData gameData) {

        GameManager manager = new GameManager(gameData.getPlayers(), gameData.getCurrentPlayer(), gameData.getRound());
        int playgroundSize = gameData.getCurrentPlayer().getPlaygroundOwn().getSize();
        ArrayList<BattleShipGui> shipPositionList = new ArrayList<>();

        ControllerPlayGame controllerPlayGame = new ControllerPlayGame(playgroundSize, shipPositionList, manager,
                GameSettings.onlyInGame(true, false));
        return controllerPlayGame;
    }

    /**
     * #############################################   init methods  ################################################
     */


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        HBox.setHgrow(vBoxOwnPlayground, Priority.ALWAYS);
        HBox.setHgrow(vBoxEnemyPlayground, Priority.ALWAYS);

        VBox.setVgrow(vBoxOwnPlayground, Priority.ALWAYS);
        VBox.setVgrow(vBoxEnemyPlayground, Priority.ALWAYS);

        generateGridPane(gridPaneOwnField);
        generateGridPane(gridPaneEnemyField);

        preallocateFieldsWithWater(gridPaneOwnField);
        preallocateFieldsWithWater(gridPaneEnemyField);

        placeOwnShipsOnOwnPlayground();

        // TODO: find better solution for HashMap keys
        this.playerGridPaneHashMap.put(1, gridPaneOwnField);
        this.playerGridPaneHashMap.put(0, gridPaneEnemyField);
        this.gameManager.startGame();
        this.startPlaygroundUpdaterThread();

        ImageCursor cursor = new ImageCursor(new Image(getClass().getResourceAsStream("/images/icons/cursor.png")));
        buttonBack.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (oldScene == null && newScene != null) {
                defaultCursor = buttonBack.getScene().getCursor();
                buttonBack.getScene().setCursor(cursor);
            }
        });
    }

    public void initFieldsFromLoad(GameData gameData) {
        Player localPLayer = gameData.getPlayers()[0];
        this.aiVsAi = localPLayer instanceof PlayerAI;
        PlaygroundInterface ownPlayground = localPLayer.getPlaygroundOwn();
        PlaygroundInterface enemyPlayground = localPLayer.getPlaygroundEnemy();

        // Own playground
        this.initPlaygroundFromLoad(ownPlayground, this.gridPaneOwnField);

        // Enemy playground
        this.initPlaygroundFromLoad(enemyPlayground, this.gridPaneEnemyField);
    }


    /**
     * GridPane is automatically generated based on predefined parameters
     *
     * @param gridPane An empty GridPane
     */

    private void generateGridPane(GridPane gridPane) {

        CELL_PERCENTAGE_WIDTH = 100 / playgroundSize;

        for (int i = 0; i < playgroundSize; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(CELL_PERCENTAGE_WIDTH);
            gridPane.getColumnConstraints().add(col);
        }

        for (int i = 0; i < playgroundSize; i++) {
            RowConstraints row = new RowConstraints();
            row.setPercentHeight(CELL_PERCENTAGE_WIDTH);
            gridPane.getRowConstraints().add(row);
        }

    }


    /**
     * Water is pre-populated on the playing fields
     *
     * @param gridPane A non empty GridPane
     */

    private void preallocateFieldsWithWater(GridPane gridPane) {

        for (int i = 0; i < this.playgroundSize; i++) {
            for (int j = 0; j < this.playgroundSize; j++) {
                generateWater(i, j, gridPane);
            }
        }
    }


    /**
     * Gui is dynamically generated in the init method and adapted according to the specifications.
     */

    private void generateWater(int possHorizontal, int possVertical, GridPane gridPane) {

        PaneExtends p = new PaneExtends(PaneExtends.FieldType.FOG);
        p.setStyle("-fx-background-color: #aaaaaa");
        p.setId("Water");
        gridPane.add(p, possHorizontal, possVertical);
        if (gridPane == this.gridPaneEnemyField) {
            this.addClickFieldEvent(p);
            if (!showHeatMap) {
                p.setId("FOG");
            } else {
                p.setId("");
            }
        }
    }


    /**
     * place own ships on own Playground in the GUI
     */

    private void placeOwnShipsOnOwnPlayground() {

        for (BattleShipGui battleShipGui : shipPositionList) {

            addShipToPlayground(battleShipGui);
        }
    }


    /**
     * The method places the graphic object on the surface.
     *
     * @param battleShipGui Object that holds all important information about the graphical object.
     */

    private void addShipToPlayground(BattleShipGui battleShipGui) {

        if (battleShipGui.getPosition().getDirection() == ShipPosition.Direction.HORIZONTAL) {

            int index = battleShipGui.getPosition().getX() * playgroundSize + battleShipGui.getPosition().getY();
            String cssId;
            for (int i = 0; i < battleShipGui.getPosition().getLength(); i++) {
                PaneExtends pane = (PaneExtends) gridPaneOwnField.getChildren().get(index);
                cssId = battleShipGui.getPosition().getLength() + "_0" + (i + 1) + "_H";
                pane.setStyle("-fx-background-color: #00ff00");
                pane.setId(cssId);
                pane.setFieldType(PaneExtends.FieldType.SHIP);
                index += playgroundSize;
            }
        }       //TODO Bilder mit 90 grad drehung müssen geladen werden
        if (battleShipGui.getPosition().getDirection() == ShipPosition.Direction.VERTICAL) {

            int index = battleShipGui.getPosition().getX() * playgroundSize + battleShipGui.getPosition().getY();
            String cssId;
            for (int i = 0; i < battleShipGui.getPosition().getLength(); i++) {

                PaneExtends pane = (PaneExtends) gridPaneOwnField.getChildren().get(index);
                cssId = battleShipGui.getPosition().getLength() + "_0" + (i + 1);
                pane.setStyle("-fx-background-color: #00ff00");
                pane.setId(cssId);
                pane.setFieldType(PaneExtends.FieldType.SHIP);
                index++;

            }
        }
    }

    private void initPlaygroundFromLoad(PlaygroundInterface playground, GridPane gridPane) {
        // All fields to water/fog
        for (int y = 0; y < playground.getSize(); y++) {
            for (int x = 0; x < playground.getSize(); x++) {
                Playground.Field field = playground.getFields()[y][x];
                Position[] pos = {new Position(x, y)};

                String cssId = "black";
                if (field.type == Playground.FieldType.SHIP) {

                    Ship ship = (Ship) field.element;
                    cssId = "red";
                    if (gridPane == gridPaneEnemyField)
                        cssId = "Water_Ship_Hit";
                }
                if (field.type == Playground.FieldType.WATER) {

                    if (!field.isHit()) {
                        cssId = "Water";
                    }
                    if (field.isHit()) {
                        cssId = "Water_Hit";
                    }
                }
                if (field.type == Playground.FieldType.FOG) {
                    cssId = "FOG";
                }
                this.color_fields(pos, cssId, gridPane);
            }
        }
        String cssID = "";
        // Ship fields
        for (Ship ship : playground.getAllShips()) {
            ShipPosition shipPosition = ship.getShipPosition();
            int shipPart = 1;
            for (Position pos : shipPosition.generateIndices()) {
                Playground.Field field = playground.getFields()[pos.getY()][pos.getX()];
                if (gridPane == gridPaneOwnField) {

                    if (ship.getShipPosition().getDirection() == ShipPosition.Direction.HORIZONTAL) {
                        cssID = ship.getShipPosition().getLength() + "_0" + shipPart + "_H";
                        if (field.isHit())
                            cssID += "_X";
                        //boolean isHit = field.isHit();
                    }

                    if (ship.getShipPosition().getDirection() == ShipPosition.Direction.VERTICAL) {
                        cssID = ship.getShipPosition().getLength() + "_0" + shipPart;
                        if (field.isHit())
                            cssID += "_X";
                        //boolean isHit = field.isHit();
                    }

                    Position[] arrPos = new Position[1];
                    arrPos[0] = pos;
                    this.color_fields(arrPos, cssID, gridPaneOwnField);
                    shipPart++;
                }
            }
        }
    }


    /**
     * ############################################# Turn #######################################################
     */

    /**
     * add Click Event to a field on the playground
     *
     * @param p Element witch the event is added
     */

    private void addClickFieldEvent(PaneExtends p) {
        if (!aiVsAi) {
            p.setOnMouseClicked(mouseEvent -> {
                int col = GridPane.getColumnIndex(p);
                int row = GridPane.getRowIndex(p);
                Position pos = new Position(col, row);
                this.gameManager.shootP1(pos);
            });
        }
    }

    /**
     * cssID field to show what kind of element it on the playground  is
     *
     * @param cssID       cssID of the field
     * @param gridPane    playground from one of the player
     * @param waterFields fields which should get a cssID
     */

    private void color_fields(Position[] waterFields, String cssID, GridPane gridPane) {

        for (Position position : waterFields) {
            PaneExtends p = this.getPaneAtPosition(gridPane, position.getX(), position.getY());
            p.setStyle("-fx-background-color: " + cssID); // später auskomentieren
            p.setId(cssID);
        }
    }

    /**
     * Permanently updates all playgrounds in the background.
     * Every time a player makes a shot.
     */

    private void startPlaygroundUpdaterThread() {

        Task task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {

                TurnResult res;
                do {
                    updateShowCurrentPlayer();
                    res = gameManager.pollTurn("GUI_1");
                    if (res != null) {
                        LoggerGUI.debug("TurnResult in GUI: " + res);
                        if (res.getError() == TurnResult.Error.NONE) {
                            if (res.getPlayerIndex() == 1 && slowAIShooting) {
                                Thread.sleep(AI_SHOOTING_DELAY_MS);
                            }
                            updateByShotResult(playerGridPaneHashMap.get(res.getPlayerIndex()), res.getSHOT_RESULT());
                        } else {
                            LoggerGUI.info("NOTIFY USER: Turn was not valid" + res.getError());
                        }
                    } else {
                        if (!Thread.currentThread().isInterrupted())
                            LoggerGUI.error("Received TurnResult with value null");
                        else {
                            // Thread was stopped
                        }
                    }
                } while (res != null && !res.isFINISHED() && !Thread.currentThread().isInterrupted());

                LoggerGUI.info("PlaygroundUpdaterThread was stopped. No more results will be displayed");
                return true;
            }
        };

        task.setOnSucceeded(e -> {
            Player player = gameManager.getCurrentPlayer();
            boolean playerHumanWins = player.getIndex() == 0;
            if (loadEndScreen) {
                loadEndScreen(playerHumanWins);
            }
            ResourcesDestructor.stopSingleThread(playgroundUpdaterThread);
        });
        playgroundUpdaterThread = new Thread(task);
        playgroundUpdaterThread.setName("GUI_PlaygroundUpdater");
        playgroundUpdaterThread.start();
        ResourcesDestructor.addThread(playgroundUpdaterThread);
    }


    /**
     * ...
     */

    private void updateByShotResult(GridPane gridPane, ShotResult shotResult) {

        String cellStyle;
        PaneExtends paneExtends = this.getPaneAtPosition(gridPane, shotResult.getPosition().getX(),
                shotResult.getPosition().getY());

        if (shotResult.getType() == Playground.FieldType.SHIP) {
            MusicPlayer.playSound(MusicPlayer.Sound.HIT_SHIP);
            ShotResultShip resultShip = (ShotResultShip) shotResult;
            cellStyle = "-fx-background-color: #ff0000";
            if (gridPane == gridPaneOwnField) {
                if (paneExtends.getId().contains("_H"))
                    paneExtends.setId(paneExtends.getId() + "_X");
                else
                    paneExtends.setId(paneExtends.getId() + "_X");
            } else {
                paneExtends.setId("Water_Ship_Hit");
            }

            if (resultShip.getStatus() == Ship.LifeStatus.SUNKEN) {
                Position[] waterFields = resultShip.getWaterFields();
                this.color_fields(waterFields, "Water_Hit", gridPane);
            }
        } else if (shotResult.getType() == Playground.FieldType.WATER) {
            MusicPlayer.playSound(MusicPlayer.Sound.HIT_WATER);
            paneExtends.setId("Water_Hit");
        } else {
            throw new Error("Invalid shotResult type: " + shotResult.getType());
        }

        if (gridPane == this.gridPaneEnemyField && this.showHeatMap) {
            this.updateHeatMap(shotResult);
        }
    }

    private void updateShowCurrentPlayer() {
        if (gameManager.getCurrentPlayer().getIndex() == 0) {
            // Self
            labelEnemyPlayground.setStyle("-fx-text-fill: grey");
            labelOwnPlayground.setStyle("-fx-text-fill: green");
        } else {
            // enemy
            labelEnemyPlayground.setStyle("-fx-text-fill: green");
            labelOwnPlayground.setStyle("-fx-text-fill: grey");
        }
    }

    /**
     * ...
     */

    private PaneExtends getPaneAtPosition(GridPane gridPane, int x, int y) {

        int index = x * playgroundSize + y;
        return (PaneExtends) gridPane.getChildren().get(index);
    }

    /**
     * ...
     */

    private void updateHeatMap(ShotResult result) {

        this.playgroundHeatmap.update(result);
        int[][] heatMap = this.playgroundHeatmap.buildHeatMap(255);
        this.playgroundHeatmap.printFields();
        PlaygroundHeatmap.printHeatMap(heatMap);
        for (int y = 0; y < playgroundSize; y++) {
            for (int x = 0; x < playgroundSize; x++) {
                PaneExtends p = getPaneAtPosition(gridPaneEnemyField, x, y);
                if (!this.playgroundHeatmap.isAlreadyDiscoveredShipAt(x, y)) {
                    int w = Math.min(255, heatMap[y][x]);
                    p.setStyle("-fx-background-color: rgb(" + w + ", " + w + ", " + w + ")");
                }
                //                else {
                //                    p.setStyle("-fx-background-color: rgb(255, 0, 0)");
                //                }
            }
        }
    }

    /**
     * ########################################## (Menu )Actions #####################################################
     */

    /**
     * ...
     */

    public void clickSaveGame() {
        saveGame();
    }

    /**
     * ...
     */

    private void saveGame() {
        saveGame(-1, false);
    }

    private void saveGame(long id, boolean useID) {


        Task<Long> t = new Task<Long>() {
            @Override
            protected Long call() {
                if (useID) {
                    return gameManager.saveGame(id);
                } else {
                    return gameManager.saveGame();
                }
            }
        };
        t.setOnSucceeded(e -> {
            try {
                Notification.create(anchorPanePlayGame)
                        .header("Spiel erfolgreich gespeichert")
                        .text("game id: " + t.get())
                        .level(Notification.NotificationLevel.DONE)
                        .autoHide(3000)
                        .show();
                LoggerGUI.info("USER INFO: Successfully saved game with id=" + t.get());
            } catch (InterruptedException | ExecutionException ex) {
                ex.printStackTrace();
            }
        });
        new Thread(t).start();
    }

    public void saveGame(long id) {
        saveGame(id, true);
    }


    /** ##########################################   Window Navigation  ############################################## */

    /**
     * go back to previous Scene
     */

    public void goBackToMainMenu() {
        buttonBack.getScene().setCursor(defaultCursor);
        LoggerGUI.info("Switch scene: PlayGame --> MainMenu");
        ResourcesDestructor.shutdownAll();
        ControllerMainMenu controllerMainMenu = new ControllerMainMenu();
        SceneLoader.loadSceneInExistingWindow(SceneLoader.GameScene.MAIN_MENU, controllerMainMenu);
    }

    /**
     * if one player wins new screen is loaded
     */

    private synchronized void loadEndScreen(boolean humanPlayerWins) {
        buttonBack.getScene().setCursor(defaultCursor);
        ResourcesDestructor.shutdownAll();
        int numRounds = gameManager.getRound();
        int numHits = gameManager.getNumHitsP1();
        int numMisses = gameManager.getNumMissesP1();
        LoggerGUI.info("Switch scene: PlayGame --> EndScreen");
        ControllerGameOver controllerGameOver = new ControllerGameOver(anchorPanePlayGame, humanPlayerWins, numRounds
                , numHits, numMisses);
        SceneLoader.loadSceneInExistingWindowWithoutButtons(SceneLoader.GameScene.GAME_OVER, controllerGameOver, "");
    }


    /**
     * ...
     */
    public void leaveGame() {
        loadEndScreen = false;

        LoggerGUI.debug("Leave game");
        ResourcesDestructor.shutdownAll();
        this.goBackToMainMenu();
    }
}