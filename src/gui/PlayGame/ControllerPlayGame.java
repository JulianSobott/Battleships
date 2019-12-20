package gui.PlayGame;

import core.GameManager;
import core.Player;
import core.Playground;
import core.Ship;
import core.communication_data.*;
import core.utils.logging.LoggerGUI;
import core.utils.logging.LoggerLogic;
import gui.ControllerMainMenu;
import gui.UiClasses.BattleShipGui;
import gui.UiClasses.PaneExtends;
import gui.WindowChange.SceneLoader;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class ControllerPlayGame implements Initializable {

    @FXML
    public GridPane gridPaneOwnField;

    @FXML
    public GridPane gridPaneEnemyField;


    @FXML
    public HBox hBoxPlaygrounds;

    @FXML
    public VBox vBoxOwnPlayground;

    @FXML
    public VBox vBoxEnemyPlayground;

    @FXML
    public Button buttonBack;


    private double CELL_PERCENTAGE_WIDTH;
    private int playgroundSize;

    ArrayList<BattleShipGui> shipPositionList;
    GameManager gameManager;

    // 0: Player1, 1: Player2
    private HashMap<Integer, GridPane> playerGridPaneHashMap = new HashMap<>();
    private Thread playgroundUpdaterThread;

    private static final String filepathBackMainMenu = "../Main_Menu.fxml";


    public ControllerPlayGame(int playgroudSize, ArrayList<BattleShipGui> shipPositionList, GameManager gameManager) {
        this.playgroundSize = playgroudSize;
        this.shipPositionList = shipPositionList;
        this.gameManager = gameManager;
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
    }


    /**
     * GridPane is automatically generated based on predefined parameters
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
        gridPane.add(p, possHorizontal, possVertical);
        if (gridPane == this.gridPaneEnemyField) {
            this.addClickFieldEvent(p);
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
            for (int i = 0; i < battleShipGui.getPosition().getLength(); i++) {
                PaneExtends pane = (PaneExtends) gridPaneOwnField.getChildren().get(index);
                pane.setStyle("-fx-background-color: #00ff00");
                pane.setFieldType(PaneExtends.FieldType.SHIP);
                index += playgroundSize;
            }
        }
        if (battleShipGui.getPosition().getDirection() == ShipPosition.Direction.VERTICAL) {

            int index = battleShipGui.getPosition().getX() * playgroundSize + battleShipGui.getPosition().getY();
            for (int i = 0; i < battleShipGui.getPosition().getLength(); i++) {

                PaneExtends pane = (PaneExtends) gridPaneOwnField.getChildren().get(index);
                pane.setStyle("-fx-background-color: #00ff00");
                pane.setFieldType(PaneExtends.FieldType.SHIP);
                index++;

            }
        }
    }


    /**
     * ############################################# Turn #######################################################
     */

    private void addClickFieldEvent(PaneExtends p) {
        p.setOnMouseClicked(mouseEvent -> {
            int col = GridPane.getColumnIndex(p);
            int row = GridPane.getRowIndex(p);
            Position pos = new Position(col, row);
            this.gameManager.shootP1(pos);
        });
    }

    private void color_fields(Position[] waterFields, String color, GridPane gridPane) {
        for (Position position : waterFields) {
            PaneExtends p = this.getPaneAtPosition(gridPane, position.getX(), position.getY());
            p.setStyle("-fx-background-color: " + color);
        }
    }

    /**
     * Permanently updates all playgrounds in the background.
     * Every time a player makes a shot.
     *
     */
    private void startPlaygroundUpdaterThread() {
        playgroundUpdaterThread = new Thread(() -> {
            TurnResult res;
            do {
                res = gameManager.pollTurn("GUI_1");
                if (res != null) {
                    LoggerGUI.info("TurnResult in GUI: " + res);
                    if (res.getError() == TurnResult.Error.NONE) {
                        this.updateByShotResult(this.playerGridPaneHashMap.get(res.getPlayerIndex()), res.getSHOT_RESULT());
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
        });
        playgroundUpdaterThread.setName("GUI_PlaygroundUpdater");
        playgroundUpdaterThread.start();
    }

    private void updateByShotResult(GridPane gridPane, ShotResult shotResult) {
        String cellStyle;
        if (shotResult.getType() == Playground.FieldType.SHIP) {
            ShotResultShip resultShip = (ShotResultShip) shotResult;
            cellStyle = "-fx-background-color: #ff0000";
            if (resultShip.getStatus() == Ship.LifeStatus.SUNKEN) {
                Position[] waterFields = resultShip.getWaterFields();
                this.color_fields(waterFields, "#0000ff", gridPane);
            }
        } else if (shotResult.getType() == Playground.FieldType.WATER) {
            cellStyle = "-fx-background-color: #0055ff";
        } else {
            throw new Error("Invalid shotResult type: " + shotResult.getType());
        }
        PaneExtends paneExtends = this.getPaneAtPosition(gridPane, shotResult.getPosition().getX(),
                shotResult.getPosition().getY());
        paneExtends.setStyle(cellStyle);
    }

    private PaneExtends getPaneAtPosition(GridPane gridPane, int x, int y) {
        int index = x * playgroundSize + y;
        return (PaneExtends) gridPane.getChildren().get(index);
    }

    /** ##########################################   Window Navigation  ############################################## */

    /**
     * go back to previous Scene
     */

    //TODO wo soll der Spieler beim Spielabruch landen ?? Wieder im Hauptmenü??
    public void goBackToMainMenu() {
        ControllerMainMenu controllerMainMenu = new ControllerMainMenu();
        SceneLoader sceneLoader = new SceneLoader(buttonBack, filepathBackMainMenu, controllerMainMenu);
        sceneLoader.loadSceneInExistingWindow();
    }


    // TODO place

    public void leaveGame() {
        LoggerGUI.debug("Leave game");
        this.exitInGameThread();
        this.goBackToMainMenu();
    }

    public void exitInGameThread() {
        LoggerGUI.debug("Num threads before exit: " + Thread.activeCount());
        this.playgroundUpdaterThread.interrupt();
        try {
            this.playgroundUpdaterThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.gameManager.exitInGameThread();
        LoggerGUI.debug("Num threads after exit: " + Thread.activeCount());
    }
}