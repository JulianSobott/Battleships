package gui.ShipPlacement;

import core.GameManager;
import core.Ship;
import core.communication_data.*;
import core.utils.ResourcesDestructor;
import core.utils.logging.LoggerGUI;
import core.utils.logging.LoggerState;
import gui.PlayGame.ControllerPlayGame;
import gui.UiClasses.BattleShipGui;
import gui.UiClasses.ButtonShip;
import gui.UiClasses.HBoxExends;
import gui.UiClasses.Notification;
import gui.WindowChange.SceneLoader;
import gui.newGame.ControllerNewGame;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import network.Connected;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class ControllerShipPlacement implements Initializable {

    private final int playgroundSize;
    private final GameManager GAME_MANAGER;
    private final ShipList SHIP_LIST;
    @FXML
    private GridPane dataGridBattleship;
    @FXML
    private VBox vBoxShips;
    @FXML
    private Button buttonBack;
    @FXML
    private Button buttonPlaceShipsRandom;
    @FXML
    private Button startGame;

    private double CELL_PERCENTAGE_WIDTH;
    private HashMap<Integer, ShipCounterPair> hashMapShipLabels = new HashMap<>();
    private ArrayList<ButtonShip> shipArrayListGui = new ArrayList<>();
    private Connected networkConnection;
    private GameSettings gameSettings;

    private boolean disableShipPlacement = false; // Set when start game clicked
    private boolean allShipsPlacedSent = false; // Needed to not send again

    /**
     * ################################################   Constructors  ################################################
     */

    public ControllerShipPlacement(GameSettings settings) {
        gameSettings = settings;
        this.GAME_MANAGER = new GameManager();
        NewGameResult res = this.GAME_MANAGER.newGame(settings);
        this.SHIP_LIST = res.getSHIP_LIST();
        this.playgroundSize = settings.getPlaygroundSize();
        this.networkConnection = settings.getNetworkConnection();
        if(settings.isAiVsAi()) {
            for(Ship s : settings.getP1().getPlaygroundOwn().getAllShips()) {
                shipArrayListGui.add(new ButtonShip(new BattleShipGui(s.getId(), s.getShipPosition())));
            }
            startGame();
        }
    }


    /**
     * ################################################   init methods  ################################################
     */


    /**
     * Gui is dynamically generated in the init method and adapted according to the specifications.
     */

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        generateGridPane();
        generateShips();
        preallocateFieldsWithWater();
    }


    /**
     * GridPane is automatically generated based on predefined parameters
     */

    private void generateGridPane() {

        CELL_PERCENTAGE_WIDTH = 100 / playgroundSize;

        for (int i = 0; i < playgroundSize; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(CELL_PERCENTAGE_WIDTH);
            dataGridBattleship.getColumnConstraints().add(col);
        }

        for (int i = 0; i < playgroundSize; i++) {
            RowConstraints row = new RowConstraints();
            row.setPercentHeight(CELL_PERCENTAGE_WIDTH);
            dataGridBattleship.getRowConstraints().add(row);
        }
    }


    /**
     * Ships are dynamically generated depending on the size of the playing field
     */

    private void generateShips() {
        ArrayList<Node> list = new ArrayList<>();
        for (ShipList.Pair pair : this.SHIP_LIST) {
            ShipCounterPair shipPair = new ShipCounterPair(pair.getNum(), pair.getSize());
            this.hashMapShipLabels.put(pair.getSize(), shipPair);
            HBoxExends hBox = createNewGuiShipType(pair.getSize());
            list.add(hBox);
        }
        vBoxShips.getChildren().addAll(list);
    }


    /**
     * Water is pre-populated on the playing fields
     */

    private void preallocateFieldsWithWater() {

        for (int i = 0; i < this.playgroundSize; i++) {
            for (int j = 0; j < this.playgroundSize; j++) {
                generateWater(i, j);
            }
        }
    }


    /**
     * Gui is dynamically generated in the init method and adapted according to the specifications.
     */

    private void generateWater(int possHorizontal, int possVertical) {
        Pane p = new Pane();
        p.setStyle("-fx-background-color: #2E64FE");
        p.setId("Welle01");
        dataGridBattleship.add(p, possHorizontal, possVertical);
        handleDragOver(p);
        handleDrop(p);

    }


    /**
     * This method generates the different types of ships that the player sees on the right side of the board
     * and can then distribute on the board.
     *
     * @param shipSize length of the ship type
     */

    private HBoxExends createNewGuiShipType(int shipSize) {

        BattleShipGui battleShipGui = new BattleShipGui(shipSize);
        HBoxExends hBox = new HBoxExends(battleShipGui);
        hBox.setSpacing(20);
        hBox.setAlignment(Pos.CENTER);

        String shipIcon = "battleship0" + shipSize;
        String path = "/gui/ShipIcons/battleship0" + shipSize + ".png";

        Image battleShipImage = new Image(path);
        ImageView imageView = new ImageView(battleShipImage);
        addEventDragDetected(hBox);
        imageView.setFitWidth(120);
        imageView.setFitHeight(100);

        hBox.getChildren().addAll(imageView, this.hashMapShipLabels.get(shipSize).getTextLabel());
        return hBox;
    }


    /**
     * #########################################   DRAG & Drop-Feature  ############################################
     */

    /**
     * This method makes it possible to place ships on the playground.
     *
     * @param hBoxBattleship ShipImage which player will place on the playground
     */

    private void addEventDragDetected(HBoxExends hBoxBattleship) {

        hBoxBattleship.setOnDragDetected(mouseEvent -> {
            if (disableShipPlacement) return;

            Dragboard dragboard = hBoxBattleship.startDragAndDrop(TransferMode.ANY);

            ClipboardContent clipboardContent = new ClipboardContent();
            DataFormat dataFormat = DataFormat.lookupMimeType("BattleShip");
            if (dataFormat == null) {
                dataFormat = new DataFormat("BattleShip");
            }
            clipboardContent.put(dataFormat, hBoxBattleship.getBattleShipGui());
            dragboard.setContent(clipboardContent);

        });
    }


    /**
     * This method tells the player whether and where to place his ship. // neu Formulieren
     *
     * @param pane Element witch receives the Drag and Drop element.
     */

    //ToDO Felder einfärben in denen das Schiff platziert wird
    private void handleDragOver(Pane pane) {

        pane.setOnDragOver(dragEvent -> dragEvent.acceptTransferModes(TransferMode.ANY));
    }

    /**
     * This Method makes it possible to shift Ships on the playground.
     *
     * @param buttonShip Placed ship on the Playground
     */

    private void addEventDragDetectedPlacedShip(ButtonShip buttonShip) {

        buttonShip.setOnDragDetected(mouseEvent -> {
            if (disableShipPlacement) return;

            Dragboard dragboard = buttonShip.startDragAndDrop(TransferMode.ANY);

            ClipboardContent clipboardContent = new ClipboardContent();
            DataFormat dataFormat = DataFormat.lookupMimeType("PlacedBattleShip");
            if (dataFormat == null) {
                dataFormat = new DataFormat("PlacedBattleShip");
            }
            clipboardContent.put(dataFormat, buttonShip.getBattleShipGui());
            dragboard.setContent(clipboardContent);
        });
    }

    /**
     * This Method makes it possible to move Ships
     *
     * @param panePlaygroundCell ShipImage which player will place on the playground
     */

    private void handleDrop(Pane panePlaygroundCell) {

        panePlaygroundCell.setOnDragDropped(dragEvent -> {
            if (disableShipPlacement) return;

            try {
                Dragboard dragboard = dragEvent.getDragboard();
                if (DataFormat.lookupMimeType("BattleShip") != null &&
                        null != dragboard.getContent(DataFormat.lookupMimeType("BattleShip"))) {
                    shipPlacementOnPlayground(dragboard, panePlaygroundCell);
                } else if (DataFormat.lookupMimeType("PlacedBattleShip") != null &&
                        null != dragboard.getContent(DataFormat.lookupMimeType("PlacedBattleShip"))) {
                    shipShiftOnPlayground(dragboard, panePlaygroundCell);
                } else {
                    LoggerGUI.error("handleDrop: dragboard Content is null: ContentType=" + dragboard.getContentTypes());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * method places Ship on Playground if possible
     *
     * @param dragboard          contains the data about the ship to be placed in the buffer memory
     * @param panePlaygroundCell Cell where the ship begins
     */

    private void shipPlacementOnPlayground(Dragboard dragboard, Pane panePlaygroundCell) {
        if (disableShipPlacement) return;
        BattleShipGui battleShipGui = getBattleshipGIUObjectFromDragboard("BattleShip", dragboard);

        int horizontalIndex = GridPane.getColumnIndex(panePlaygroundCell);
        int verticalIndex = GridPane.getRowIndex(panePlaygroundCell);

        ButtonShip button = generateNewBattleship(battleShipGui);

        PlaceShipResult res = this.GAME_MANAGER.placeShip(new ShipPosition(horizontalIndex, verticalIndex,
                battleShipGui.getPosition().getDirection(), battleShipGui.getPosition().getLength()));
        if (res.isSuccessfullyPlaced()) {

            addShipToPlayground(button, battleShipGui, res);
            this.hashMapShipLabels.get(battleShipGui.getPosition().getLength()).decreaseCounter();
        } else {
            //TODO: no ships are left
            String title = "Not allowed to place here a Ship";
            String content = "Parts of the ship collapse either with another ship or the edge of the field";
            ShipCounterPair shipCounterPair = this.hashMapShipLabels.get(battleShipGui.getPosition().getLength());
            if (shipCounterPair.getCounter() == 0) {

                title = "no length " + battleShipGui.getPosition().getLength() + " vessel available";
                content = "You have already placed all ships of this size on the board";
            }
            Notification.create(panePlaygroundCell)
                    .header(title)
                    .text(content)
                    .level(Notification.NotificationLevel.WARNING)
                    .autoHide(6000)
                    .show();
        }
    }

    /**
     * method shifts Ship on a new Playground position if possible
     *
     * @param dragboard          contains the data about the ship to be placed in the buffer memory
     * @param panePlaygroundCell Cell where the ship begins
     */

    // TODO: rename
    private void shipShiftOnPlayground(Dragboard dragboard, Pane panePlaygroundCell) {
        if (disableShipPlacement) return;

        BattleShipGui battleShipGui = getBattleshipGIUObjectFromDragboard("PlacedBattleShip", dragboard);

        int col = GridPane.getColumnIndex(panePlaygroundCell);
        int row = GridPane.getRowIndex(panePlaygroundCell);

        ShipPosition oldPos = battleShipGui.getPosition();
        ShipPosition pos = new ShipPosition(col, row, oldPos.getDirection(), oldPos.getLength());

        PlaceShipResult res = this.GAME_MANAGER.moveShip(battleShipGui.getShipID(), pos);
        if (res.isSuccessfullyPlaced()) {

            deleteShipFromPlayground(battleShipGui);

            ButtonShip buttonShip = generateNewBattleship(battleShipGui);
            addShipToPlayground(buttonShip, battleShipGui, res);

        } else {
            LoggerGUI.info("[USER HINT] Cannot move ship to new position: " + res.getERROR());
            Notification.create(panePlaygroundCell)
                    .header("Cannot move ship to new position")
                    .text("Parts of the ship collapse either with another ship or the edge of the field")
                    .level(Notification.NotificationLevel.WARNING)
                    .autoHide(6000)
                    .show();
        }
    }

    /**
     * Method deletes GuiShip from playground
     *
     * @param battleShipGui contains the information which ship has to be discharged
     */

    private void deleteShipFromPlayground(BattleShipGui battleShipGui) {
        if (disableShipPlacement) return;

        ButtonShip buttonShipDelete = null;
        for (ButtonShip buttonShip : shipArrayListGui) {

            if (battleShipGui.getShipID().equals(buttonShip.getBattleShipGui().getShipID())) {
                buttonShipDelete = buttonShip;
                dataGridBattleship.getChildren().remove(buttonShipDelete);
            }
        }
        shipArrayListGui.remove(buttonShipDelete);
    }


    /**
     * The method gets the object from the Dragboard.
     *
     * @param identifier holds the key to get Object
     */

    private BattleShipGui getBattleshipGIUObjectFromDragboard(String identifier, Dragboard dragboard) {

        Object o = dragboard.getContent(DataFormat.lookupMimeType(identifier));

        BattleShipGui battleShipGui = null;
        if (o instanceof BattleShipGui) {
            battleShipGui = (BattleShipGui) o;
        } else {
            LoggerGUI.error("Dragboard content is not instanceof BattleshipGui. Type is: " + o.getClass());
            throw new ClassCastException("Dragboard content is not instanceof BattleshipGui");
        }
        return battleShipGui;
    }


    /**
     * generates a new Gui Ship on Playground
     *
     * @param battleShipGui holds all information about the ship
     */

    private ButtonShip generateNewBattleship(BattleShipGui battleShipGui) {

        ButtonShip button = new ButtonShip(battleShipGui);
        button.setStyle("-fx-background-color: #2E64FE");
       // button.setId("Battleship01");
        addEventDragDetectedPlacedShip(button);
        addContextMenu(button);

        return button;
    }


    /**
     * add ContextMenu to Ships on Battlefield
     *
     * @param button to add the event
     */

    private void addContextMenu(ButtonShip button) {

        ContextMenu contextMenu = new ContextMenu();

        MenuItem item1 = new MenuItem("Schiff löschen");
        addMenuItemDeleteShip(item1, button);

        MenuItem item2 = new MenuItem("Schiff drehen");
        addMenuItemTurnShip(item2, button);

        contextMenu.getItems().addAll(item1, item2);
        button.setContextMenu(contextMenu);
    }


    /** ##########################################   SHIPS CONTEXT-MENU  ############################################ */

    /**
     * The method places the graphic object on the surface.
     *
     * @param buttonShip    Graphical object to be placed
     * @param battleShipGui Object that holds all important information about the graphical object.
     * @param result        the result contains the new information about the graphical object.
     */

    private void addShipToPlayground(ButtonShip buttonShip, BattleShipGui battleShipGui, PlaceShipResult result) {
        if (disableShipPlacement) return;

        battleShipGui.setPosition(result.getPosition());
        battleShipGui.setShipID(result.getShipID());
        String cssID;

        if (battleShipGui.getPosition().getDirection() == ShipPosition.Direction.HORIZONTAL) {
            cssID = battleShipGui.getPosition().getLength() + "_H";
            buttonShip.setId(cssID);
            dataGridBattleship.add(buttonShip, result.getPosition().getX(), result.getPosition().getY(), battleShipGui.getPosition().getLength(), 1);
        }
        if (battleShipGui.getPosition().getDirection() == ShipPosition.Direction.VERTICAL) {
            cssID = battleShipGui.getPosition().getLength() + "_V";
            buttonShip.setId(cssID);
            dataGridBattleship.add(buttonShip, result.getPosition().getX(), result.getPosition().getY(), 1, battleShipGui.getPosition().getLength());
        }
        buttonShip.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        shipArrayListGui.add(buttonShip);
    }

    /**
     * add functionality to MenuItemDeleteShip
     *
     * @param menuItemDeleteShip to add the event
     * @param buttonShip         Gui Ship Object
     */

    private void addMenuItemDeleteShip(MenuItem menuItemDeleteShip, Button buttonShip) {

        menuItemDeleteShip.setOnAction(event -> {
            if (disableShipPlacement) return;

            int index = dataGridBattleship.getChildren().indexOf(buttonShip);
            Node nodeShip = dataGridBattleship.getChildren().get(index);
            ButtonShip buttonShip1 = null;
            if (nodeShip instanceof ButtonShip) {
                buttonShip1 = (ButtonShip) nodeShip;
                deleteShipFromPlayground(buttonShip1);
            } else {
                LoggerGUI.error("Object is not instance of ButtonShip: " + nodeShip);
            }
        });
    }

    /**
     * ######################################   random Ship Placement  ##############################################
     */

    /**
     * Ships can be turned on the playground
     *
     * @param menuItemTurnShip menu Item of the ship
     * @param buttonShip       ship which should be turned
     */

    private void addMenuItemTurnShip(MenuItem menuItemTurnShip, ButtonShip buttonShip) {
        menuItemTurnShip.setOnAction(event -> {
            if (disableShipPlacement) return;

            int horizontalIndex = GridPane.getColumnIndex(buttonShip);
            int verticalIndex = GridPane.getRowIndex(buttonShip);

            BattleShipGui battleShipGui = buttonShip.getBattleShipGui();

            int colspan, rowspan;
            ShipPosition.Direction directionNew;

            if (battleShipGui.getPosition().getDirection() == ShipPosition.Direction.HORIZONTAL) {
                colspan = 1;
                rowspan = battleShipGui.getPosition().getLength();
                directionNew = ShipPosition.Direction.VERTICAL;
            } else {
                colspan = battleShipGui.getPosition().getLength();
                rowspan = 1;
                directionNew = ShipPosition.Direction.HORIZONTAL;
            }

            ShipPosition posOld = battleShipGui.getPosition();
            ShipPosition position = new ShipPosition(posOld.getX(), posOld.getY(),
                    directionNew, posOld.getLength());

            PlaceShipResult res = GAME_MANAGER.moveShip(battleShipGui.getShipID(), position);

            if (res.isSuccessfullyPlaced()) {
                dataGridBattleship.getChildren().remove(buttonShip);
                dataGridBattleship.add(buttonShip, horizontalIndex, verticalIndex, colspan, rowspan);
                battleShipGui.getPosition().setDirection(directionNew);
                if(directionNew == ShipPosition.Direction.HORIZONTAL)
                    buttonShip.setId(buttonShip.getBattleShipGui().getPosition().getLength() + "_H");
                else
                    buttonShip.setId(buttonShip.getBattleShipGui().getPosition().getLength() + "_V");
            } else {
                LoggerGUI.info("User Info: Not allowed to rotate ship");
                LoggerGUI.info("[USER HINT] Cannot move ship to new position: " + res.getERROR());
                Notification.create(buttonBack)
                        .header("Not allowed to rotate ship")
                        .text("Parts of the ship collapse either with another ship or the edge of the field")
                        .level(Notification.NotificationLevel.WARNING)
                        .autoHide(6000)
                        .show();
            }
        });
    }

    /**
     * ...
     */

    @FXML
    public void placeShipsRandom() {
        if (disableShipPlacement) return;

        if (shipArrayListGui.size() > 0) {
            while (shipArrayListGui.size() > 0) {
                BattleShipGui battleShipGui = shipArrayListGui.get(0).getBattleShipGui();
                deleteShipFromPlayground(battleShipGui);
            }
        }

        PlaceShipsRandomRes res = GAME_MANAGER.placeShipsRandom();
        if (res.isSuccessfully()) {
            for (PlaceShipsRandomRes.ShipData ship : res.getShipData()) {

                BattleShipGui battleShipGui = new BattleShipGui(ship.getId(), ship.getPOSITION());
                ButtonShip buttonShip = generateNewBattleship(battleShipGui);
                PlaceShipResult placeShipResult = new PlaceShipResult(true, ship.getPOSITION(), ship.getId(), PlaceShipResult.Error.NONE);
                addShipToPlayground(buttonShip, battleShipGui, placeShipResult);
            }
            for (ShipCounterPair lbl : this.hashMapShipLabels.values()) {
                lbl.setCounter(0);
            }
        } else {
            LoggerGUI.info("[USER HINT]: Can not place ships random");
            // TODO: inform user

        }

    }

    /**
     * ...
     */

    @FXML
    private void deletePlacedShips() {
        if (disableShipPlacement) return;

        while (!shipArrayListGui.isEmpty()) {
            ButtonShip buttonShip = shipArrayListGui.remove(0);
            deleteShipFromPlayground(buttonShip);
        }
    }


    /** ##########################################   Window Navigation  ############################################## */

    /**
     * go back to previous Scene
     */

    @FXML
    public void BackToSettings() {
        ResourcesDestructor.shutdownAll();
        LoggerGUI.info("Switch scene: ShipPlacement --> NewGame");
        SceneLoader.loadSceneInExistingWindow(SceneLoader.GameScene.NEW_GAME, new ControllerNewGame());

    }

    /**
     * ...
     */

    private boolean deleteShipFromPlayground(ButtonShip buttonShip) {
        if (disableShipPlacement) return false;

        boolean success = GAME_MANAGER.deleteShip(buttonShip.getBattleShipGui().getShipID());
        if (success) {
            dataGridBattleship.getChildren().remove(buttonShip);
            shipArrayListGui.remove(buttonShip);
            this.hashMapShipLabels.get(buttonShip.getBattleShipGui().getPosition().getLength()).increaseCounter();
        } else {
            LoggerGUI.error("Can't delete ship: shipID=" + buttonShip.getBattleShipGui().getShipID());
        }
        return success;
    }

    /**
     * Start game against enemy
     */
    @FXML
    public void startGame() {

        GAME_MANAGER.getPlayers()[0].getPlaygroundOwn().printField();
        if (GAME_MANAGER.getPlayers()[0].areAllShipsPlaced()) {
            this.disableShipPlacement = true;
            if (networkConnection != null && !allShipsPlacedSent) {
                networkConnection.sendAllShipsPlaced();
                allShipsPlacedSent = true;
            }
        } else {
            Notification.create(buttonBack)
                    .header("Nicht alle Schiffe platziert")
                    .text("Du musst alle Schiffe platzieren, bevor du mit Spielen beginnen kannst")
                    .level(Notification.NotificationLevel.WARNING)
                    .autoHide(3000)
                    .show();
            LoggerGUI.info("Player hasn't placed all ships. Can't start game");
            return;
        }
        if (GAME_MANAGER.allPlayersReady() == StartShootingRes.ENEMY_NOT_ALL_SHIPS_PLACED) {
            Notification.create(buttonBack)
                    .header("Warten auf Gegner")
                    .text("Dein Gegner hat noch nicht alle Schiffe platziert. Sobald er alle " +
                            "platziert hat, startet das Spiel automatisch.")
                    .level(Notification.NotificationLevel.INFO)
                    .autoHide(3000)
                    .show();
        }
        Task<Void> t = new Task<Void>() {
            @Override
            protected Void call() {
                StartShootingRes res;
                StartShootingRes lastRes = null;
                while ((res = GAME_MANAGER.allPlayersReady()) != StartShootingRes.SHOOTING_ALLOWED) {
                    if (lastRes != res) {
                        LoggerGUI.info("[user hint] " + res);
                    }
                    lastRes = res;
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        };
        Thread thread = new Thread(t);
        t.setOnSucceeded(workerStateEvent -> {
            ArrayList<BattleShipGui> shipPositionList = new ArrayList<>();
            for (ButtonShip buttonShip : shipArrayListGui) {

                shipPositionList.add(buttonShip.getBattleShipGui());
            }

            LoggerState.info("Switch state to In_Game");
            ControllerPlayGame controllerPlayGame = new ControllerPlayGame(playgroundSize, shipPositionList,
                    this.GAME_MANAGER, gameSettings);
            if (this.networkConnection != null) {
                this.networkConnection.enterInGame(controllerPlayGame);
            }
            LoggerGUI.info("Switch scene: ShipPlacement --> PlayGame");
            SceneLoader.loadSceneInExistingWindow(SceneLoader.GameScene.PLAY_GAME, controllerPlayGame);
            ResourcesDestructor.stopSingleThread(thread);
        });
        thread.start();
        ResourcesDestructor.addThread(thread);
    }


    /**
     * #########################################   ShipCounterPair class   #############################################
     */


    /**
     * ...
     */

    static class ShipCounterPair {
        private final int size;
        private Label textLabel;
        private int counter;

        ShipCounterPair(int counter, int size) {
            this.counter = counter;
            this.size = size;
            this.textLabel = new Label(this.getText());
        }

        Label getTextLabel() {
            return textLabel;
        }

        public int getCounter() {
            return counter;
        }

        private void setCounter(int counter) {
            this.counter = counter;
            this.textLabel.setText(this.getText());
        }

        void decreaseCounter() {
            this.setCounter(this.counter - 1);
        }

        void increaseCounter() {
            this.setCounter(this.counter + 1);
        }

        private String getText() {
            return this.counter + " x " + this.size + "-er Shiffe";
        }
    }
}
