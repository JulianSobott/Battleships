package gui.ShipPlacement;

import core.GameManager;
import core.communication_data.*;
import core.utils.Logger;
import gui.UiClasses.BattleShipGui;
import gui.UiClasses.ButtonShip;
import gui.UiClasses.HBoxExends;
import gui.WindowChange.SceneLoader;
import gui.newGame.ControllerGameType;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class ControllerShipPlacement implements Initializable {


    //TODO auslagern in eigene Klasse --> Übersichtlichkeit ????

    class ShipCounterPair {
        private String text;
        private Label textLabel;
        private int counter;
        private final int size;

        public ShipCounterPair(int counter, int size) {
            this.counter = counter;
            this.size = size;
            this.textLabel = new Label(this.getText());
        }

        public Label getTextLabel() {
            return textLabel;
        }

        public int getCounter() {
            return counter;
        }

        private void setCounter(int counter) {
            this.counter = counter;
            this.textLabel.setText(this.getText());
        }

        public void decreaseCounter() {
            this.setCounter(this.counter - 1);
        }

        public void increaseCounter() {
            this.setCounter(this.counter + 1);
        }

        private String getText() {
            this.text = this.counter + " x " + this.size + "-er Shiffe";
            return this.text;
        }
    }


    @FXML
    private GridPane dataGridBattleship;

    @FXML
    private VBox vBoxShips;

    @FXML
    private Button buttonBack;

    private final int playgroundSize;
    private double CELL_PERCENTAGE_WIDTH;

    private final GameManager GAME_MANAGER;
    private final ShipList SHIP_LIST;

    private HashMap<Integer, ShipCounterPair> hashMapShipLabels = new HashMap<>();

    private ArrayList<ButtonShip> shipArrayListGui = new ArrayList<>();

    private static final String filepathBackNewGame = "../newGame/GameType.fxml";
    private static final String filepathPlayground = "../";

    /**
     * ################################################   Constructors  ################################################
     */

    public ControllerShipPlacement(GameSettings settings) {
        this.GAME_MANAGER = new GameManager();
        NewGameResult res = this.GAME_MANAGER.newGame(settings);
        this.SHIP_LIST = res.getSHIP_LIST();
        this.playgroundSize = settings.getPlaygroundSize();
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
     * Ships are dynamically generated depending on the size of the playing field
     */

    private void generateShips() {
        ArrayList<Node> list = new ArrayList<>();
        for (ShipList.Pair pair : this.SHIP_LIST) {
            ShipCounterPair shipPair = new ShipCounterPair(pair.getNum(), pair.getSize());
            this.hashMapShipLabels.put(pair.getSize(), shipPair);
            HBoxExends hBox = createNewGuiShip(pair.getSize(), pair.getNum());
            list.add(hBox);
        }
        vBoxShips.getChildren().addAll(list);
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

        Image battleShipImage = new Image("/gui/ShipIcons/Wasser_Groß.jpg");
        BackgroundImage im = new BackgroundImage(battleShipImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        // dataGridBattleship.setBackground(new Background(im));
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
        dataGridBattleship.add(p, possHorizontal, possVertical);
        handleDragOver(p);
        handleDrop(p);

    }

    /**
     * generates the available graphical ship elements for the GUI.
     *
     * @param shipSize      length of the ship type
     * @param numberOfShips number of ships
     */

    private HBoxExends createNewGuiShip(int shipSize, int numberOfShips) {

        BattleShipGui battleShipGui = new BattleShipGui(shipSize);
        HBoxExends hBox = new HBoxExends(battleShipGui);
        hBox.setSpacing(20);
        hBox.setAlignment(Pos.CENTER);

        Image battleShipImage = new Image("/gui/ShipIcons/Testschiff.png");
        ImageView imageView = new ImageView(battleShipImage);
        addEventDragDetected(hBox);
        imageView.setFitWidth(140);
        imageView.setFitHeight(60);

        hBox.getChildren().addAll(imageView, this.hashMapShipLabels.get(shipSize).getTextLabel());
        return hBox;
    }


    /** #########################################   DRAG & Drop-Feature  ############################################ */


    /**
     * This Method makes it possible to move Ships
     *
     * @param hBoxBattleship ShipImage which player will place on the playground
     */

    private void addEventDragDetected(HBoxExends hBoxBattleship) {

        hBoxBattleship.setOnDragDetected(mouseEvent -> {
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
     * This Method makes it possible to shift Ships on the Playground
     *
     * @param buttonShip Placed ship on the Playground
     */

    private void addEventDragDetectedPlacedShip(ButtonShip buttonShip) {

        buttonShip.setOnDragDetected(mouseEvent -> {
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
     * @param imageView Element witch receives the Drag and Drop element.
     */

    //ToDO Felder einfärben in denen das Schiff platziert wird
    private void handleDragOver(Pane imageView) {

        imageView.setOnDragOver(dragEvent -> dragEvent.acceptTransferModes(TransferMode.ANY));
    }


    /**
     * This Method makes it possible to move Ships
     *
     * @param panePlaygroundCell ShipImage which player will place on the playground
     */

    private void handleDrop(Pane panePlaygroundCell) {

        panePlaygroundCell.setOnDragDropped(dragEvent -> {
            Dragboard dragboard = dragEvent.getDragboard();

            Object o;
            if (null != dragboard.getContent(DataFormat.lookupMimeType("BattleShip"))) {

                shipPlacementOnPlayground(dragboard, panePlaygroundCell);

            } else if (null != dragboard.getContent(DataFormat.lookupMimeType("PlacedBattleShip"))) {

                shipShiftOnPlayground(dragboard, panePlaygroundCell);
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

        Object o = dragboard.getContent(DataFormat.lookupMimeType("BattleShip"));

        BattleShipGui battleShipGui = null;
        if (o instanceof BattleShipGui) {
            battleShipGui = (BattleShipGui) o;
        }

        int horizontalIndex = GridPane.getColumnIndex(panePlaygroundCell);
        int verticalIndex = GridPane.getRowIndex(panePlaygroundCell);

        ButtonShip button = generateNewBattleship(battleShipGui);

        PlaceShipResult res = this.GAME_MANAGER.placeShip(new ShipPosition(horizontalIndex, verticalIndex,
                battleShipGui.getPosition().getDirection(), battleShipGui.getPosition().getLength()));
        Logger.debug(res);
        if (res.isSuccessfullyPlaced()) {
            battleShipGui.setPosition(res.getPosition());
            battleShipGui.setShipID(res.getShipID());
            dataGridBattleship.add(button, horizontalIndex, verticalIndex, battleShipGui.getPosition().getLength(), 1);
            button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            shipArrayListGui.add(button);

            this.hashMapShipLabels.get(battleShipGui.getPosition().getLength()).decreaseCounter();
        } else {
            // TODO: inform user about failure
        }
    }

    /**
     * method shifts Ship on a new Playground position if possible
     *
     * @param dragboard          contains the data about the ship to be placed in the buffer memory
     * @param panePlaygroundCell Cell where the ship begins
     */

    private void shipShiftOnPlayground(Dragboard dragboard, Pane panePlaygroundCell) {

        Object o1 = dragboard.getContent(DataFormat.lookupMimeType("PlacedBattleShip"));
        BattleShipGui battleShipGui = null;
        if (o1 instanceof BattleShipGui) {
            battleShipGui = (BattleShipGui) o1;
        } else {
            throw new NullPointerException("BattleshipGui object not set");
        }

        int col = GridPane.getColumnIndex(panePlaygroundCell);
        int row = GridPane.getRowIndex(panePlaygroundCell);

        ShipPosition oldPos = battleShipGui.getPosition();
        ShipPosition pos = new ShipPosition(col, row, oldPos.getDirection(), oldPos.getLength());
        PlaceShipResult res = this.GAME_MANAGER.moveShip(battleShipGui.getShipID(), pos);
        if (res.isSuccessfullyPlaced()) {
            ButtonShip buttonShipDelete = null;

            /**  -----------------------------deletes Ship on old Position --------------------------------- */

            for (ButtonShip buttonShip : shipArrayListGui) {

                if (battleShipGui.getShipID().equals(buttonShip.getBattleShipGui().getShipID())) {
                    buttonShipDelete = buttonShip;
                    dataGridBattleship.getChildren().remove(buttonShipDelete);
                }
            }


            /** ---------------------------Adds Ship on new Position ------------------------------------ */
            battleShipGui.setPosition(res.getPosition());
            ButtonShip button = generateNewBattleship(battleShipGui);
            shipArrayListGui.add(button);

            if (battleShipGui.getPosition().getDirection() == ShipPosition.Direction.HORIZONTAL)
                dataGridBattleship.add(button, col, row, battleShipGui.getPosition().getLength(), 1);
            button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            if (battleShipGui.getPosition().getDirection() == ShipPosition.Direction.VERTICAL) {
                dataGridBattleship.add(button, col, row, 1, battleShipGui.getPosition().getLength());
                button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            }

            shipArrayListGui.remove(buttonShipDelete);
        } else {
            Logger.debug("[USER HINT] Cannot move ship to new position: " + res.getERROR());
            // TODO: inform user
        }
    }


    /**
     * generates a new Gui Ship on Playground
     *
     * @param battleShipGui holds all information about the ship
     */

    private ButtonShip generateNewBattleship(BattleShipGui battleShipGui) {

        ButtonShip button = new ButtonShip(battleShipGui);
        button.setStyle("-fx-background-color: #00ff00");
        addEventDragDetectedPlacedShip(button);
        addContextMenu(button);

        return button;
    }


    /** ##########################################   SHIPS CONTEXT-MENU  ############################################ */


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

    /**
     * add functionality to MenuItemDeleteShip
     *
     * @param menuItemDeleteShip to add the event
     * @param buttonShip         Gui Ship Object
     */

    private void addMenuItemDeleteShip(MenuItem menuItemDeleteShip, Button buttonShip) {

        menuItemDeleteShip.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                int index = dataGridBattleship.getChildren().indexOf(buttonShip);
                Node nodeShip = dataGridBattleship.getChildren().get(index);
                ButtonShip buttonShip = null;
                if (nodeShip instanceof ButtonShip) {
                    buttonShip = (ButtonShip) nodeShip;
                }

                boolean success = GAME_MANAGER.deleteShip(buttonShip.getBattleShipGui().getShipID());
                if (success) {
                    hashMapShipLabels.get(buttonShip.getBattleShipGui().getPosition().getLength()).increaseCounter();
                    dataGridBattleship.getChildren().remove(index);
                } else {
                    // TODO: inform user
                }
            }
        });
    }

    private void addMenuItemTurnShip(MenuItem menuItemTurnShip, ButtonShip buttonShip) {
        menuItemTurnShip.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

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
                }
            }
        });
    }


    /** ###########################################   Window Navigation  ############################################ */

    /**
     * go back to previous Scene
     */

    @FXML
    public void BackToSettings() {

        ControllerGameType controllerGameType = new ControllerGameType();
        SceneLoader sceneLoader = new SceneLoader(buttonBack, filepathBackNewGame, controllerGameType);
        sceneLoader.loadSceneInExistingWindow();

    }

}
