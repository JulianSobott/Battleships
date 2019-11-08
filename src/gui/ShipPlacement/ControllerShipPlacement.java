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
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
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
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class ControllerShipPlacement implements Initializable {

    @FXML
    private GridPane dataGridBattleship;

    @FXML
    private VBox vBoxShips;

    @FXML
    private Button buttonBack;

    private final int playgroundSize;
    private final GameManager GAME_MANAGER;
    private final ShipList SHIP_LIST;

//    private Label labelShipCounterBattleshipX5 = new Label();
//    private Label labelShipCounterBattleshipX4 = new Label();
//    private Label labelShipCounterBattleshipX3 = new Label();
//    private Label labelShipCounterBattleshipX2 = new Label();
//    private Label labelShipCounterBattleshipX1 = new Label();

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

        public void decreaseCounter(){
            this.setCounter(this.counter - 1);
        }

        public void increaseCounter(){
            this.setCounter(this.counter + 1);
        }

        private String getText(){
            this.text = this.counter + " x " + this.size + "-er Shiffe";
            return this.text;
        }
    }

    private HashMap<Integer, ShipCounterPair> hashMapShipLabels = new HashMap<>();




    private static final String numberOfBoningShipsX5 = " x " + " 5-er Schiff";
    private static final String numberOfBoningShipsX4 = " x " + " 4-er Schiff";
    private static final String numberOfBoningShipsX3 = " x " + " 3-er Schiff";
    private static final String numberOfBoningShipsX2 = " x " + " 2-er Schiff";
    private static final String numberOfBoningShipsX1 = " x " + " 1-er Schiff";

    int numberOfBoningShipsX5Counter = 2;
    int numberOfBoningShipsX4Counter = 1;
    int numberOfBoningShipsX3Counter = 3;
    int numberOfBoningShipsX2Counter = 4;
    int numberOfBoningShipsX1Counter = 2;

    // ToDO Woher weiß man welcher Schiff länge gesetzt wurde ...

    //ToDO pool (Arraylist) mit ID's welche dann auf verfügbarkeit geprüft werden und bei erlaubter platzierung einem Schiff zugeordnet werden...

    //ToDO Schiff muss noch per Drag and Drop verschoben werden können

    //ToDo Dynamisches anpassen der Spielfeldgröße....


    private static final String filepathBackNewGame = "../newGame/GameType.fxml";
    private static final String filepathPlayground = "../";


    private ArrayList availableShips = new ArrayList();

    private ArrayList<Label> availableShipTypes = new ArrayList(); // Absteigend nach größe sortiert

    private ArrayList<BattleShipGui> battleShips = new ArrayList();


    public ControllerShipPlacement(GameSettings settings) {
        this.GAME_MANAGER = new GameManager();
        NewGameResult res = this.GAME_MANAGER.newGame(settings);
        this.SHIP_LIST = res.getSHIP_LIST();
        this.playgroundSize = settings.getPlaygroundSize();
    }

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

    /**
     * Gui is dynamically generated in the init method and adapted according to the specifications.
     */

    private void generateWater(int possHorizontal, int possVertical) {
        Pane p = new Pane();
        dataGridBattleship.add(p, possHorizontal, possVertical);
        handleDragOver(p);
        handleDrop(p);

//        Image battleShipImage = new Image("/gui/ShipIcons/Wasser_Groß.jpg");
//        ImageView imageView = new ImageView(battleShipImage);
//        imageView.setPreserveRatio(false);
//
//        imageView.fitWidthProperty().bind(p.widthProperty());
//        imageView.fitHeightProperty().bind(p.heightProperty());
//        p.getChildren().add(imageView);
    }


    /**
     * Water is pre-populated on the playing fields
     */

    private void preallocateFieldsWithWater() {
        Image battleShipImage = new Image("/gui/ShipIcons/Wasser_Groß.jpg");
        ImageView imageView = new ImageView(battleShipImage);
        BackgroundImage im = new BackgroundImage(battleShipImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);

        // dataGridBattleship.setBackground(new Background(im));

        for (int i = 0; i < this.playgroundSize; i++) {
            for (int j = 0; j < this.playgroundSize; j++) {
                generateWater(i, j);
            }
        }
    }


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
     * This Method makes it possible to move Ships
     *
     * @param imageView Element witch receives the Drag and Drop element.
     */

    private void handleDragOver(Pane imageView) {

        imageView.setOnDragOver(dragEvent -> dragEvent.acceptTransferModes(TransferMode.ANY));
    }

    /**
     * This Method makes it possible to move Ships
     *
     * @param imageView ShipImage which player will place on the playground
     */

    private void handleDrop(Pane imageView) {

        imageView.setOnDragDropped(dragEvent -> {
            Dragboard dragboard = dragEvent.getDragboard();

            Object o = dragboard.getContent(DataFormat.lookupMimeType("BattleShip"));
            BattleShipGui battleShipGui = null;
            if (o instanceof BattleShipGui) {
                battleShipGui = (BattleShipGui) o;
            }

            int horizontalIndex = GridPane.getColumnIndex(imageView);
            int verticalIndex = GridPane.getRowIndex(imageView);

            ButtonShip button = new ButtonShip(battleShipGui);
            button.setStyle("-fx-background-color: #00ff00");

            addContextMenu(button);

            PlaceShipResult res = this.GAME_MANAGER.placeShip(new ShipPosition(horizontalIndex, verticalIndex,
                    battleShipGui.getPosition().getDIRECTION(), battleShipGui.getPosition().getLENGTH()));
            Logger.debug(res);
            if(res.isSuccessfullyPlaced()){
                battleShipGui.setPosition(res.getPosition());
                battleShipGui.setShipID(res.getShipID());
                dataGridBattleship.add(button, horizontalIndex, verticalIndex, battleShipGui.getPosition().getLENGTH(), 1);
                button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

                this.hashMapShipLabels.get(battleShipGui.getPosition().getLENGTH()).decreaseCounter();
            }else{
                // TODO: inform user about failure
            }

        });

    }


    /**
     * add ContextMenu to Ships on Battlefield
     *
     * @param button to add the event
     */

    //ToDO derzeit wird Schiff um 90° nach rechts gedreht. Bild leider nicht...
    private void addContextMenu(Button button) {

        // Create ContextMenu
        ContextMenu contextMenu = new ContextMenu();

        MenuItem item1 = new MenuItem("Schiff löschen");
        item1.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                int horizontalIndex = GridPane.getColumnIndex(button);
                int verticalIndex = GridPane.getRowIndex(button);

                int index = dataGridBattleship.getChildren().indexOf(button);
                Node nodeShip = dataGridBattleship.getChildren().get(index);
                ButtonShip buttonShip = null;
                if (nodeShip instanceof ButtonShip) {
                    buttonShip = (ButtonShip) nodeShip;
                }

                boolean success = GAME_MANAGER.deleteShip(buttonShip.getBattleShipGui().getShipID());
                if(success){
                    hashMapShipLabels.get(buttonShip.getBattleShipGui().getPosition().getLENGTH()).increaseCounter();
                    dataGridBattleship.getChildren().remove(index);
                }else{
                    // TODO: inform user
                }
            }
        });
        MenuItem item2 = new MenuItem("Schiff drehen");
        item2.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                int horizontalIndex = GridPane.getColumnIndex(button);
                int verticalIndex = GridPane.getRowIndex(button);

                int index = dataGridBattleship.getChildren().indexOf(button);
                Node node = dataGridBattleship.getChildren().get(index);

                ButtonShip buttonShip = null;
                if (node instanceof ButtonShip) {
                    buttonShip = (ButtonShip) node;
                }
                BattleShipGui battleShipGui = buttonShip.getBattleShipGui();

                int colspan, rowspan;
                ShipPosition.Direction directionNew;

                if (battleShipGui.getPosition().getDIRECTION() == ShipPosition.Direction.HORIZONTAL) {
                    colspan = 1;
                    rowspan = battleShipGui.getPosition().getLENGTH();
                    directionNew = ShipPosition.Direction.VERTICAL;
                } else {
                    colspan = battleShipGui.getPosition().getLENGTH();
                    rowspan = 1;
                    directionNew = ShipPosition.Direction.HORIZONTAL;
                }

                ShipPosition posOld = battleShipGui.getPosition();
                ShipPosition position = new ShipPosition(posOld.getX(), posOld.getY(),
                        directionNew, posOld.getLENGTH());
                PlaceShipResult res = GAME_MANAGER.moveShip(battleShipGui.getShipID(), position);

                if(res.isSuccessfullyPlaced()){
                    dataGridBattleship.getChildren().remove(index);
                    dataGridBattleship.add(node, horizontalIndex, verticalIndex, colspan, rowspan);
                    battleShipGui.getPosition().setDIRECTION(directionNew);
                }
            }
        });

        contextMenu.getItems().addAll(item1, item2);
        button.setContextMenu(contextMenu);
    }


    /**
     * go back to previous Scene
     */

    @FXML
    public void BackToSettings() {

        ControllerGameType controllerGameType = new ControllerGameType();
        SceneLoader sceneLoader = new SceneLoader(buttonBack, filepathBackNewGame, controllerGameType);
        sceneLoader.loadSceneInExistingWindow();

    }

    private void generateGridPane(){
        double cellWidth = dataGridBattleship.getWidth() / this.playgroundSize;
        double cellHeight = dataGridBattleship.getHeight() / this.playgroundSize;

        for(int y = 0; y < this.playgroundSize; y++){
            for(int x = 0; x < this.playgroundSize; x++) {
                // TODO:
            }
        }
    }

    private void generateShips(){
        ArrayList<Node> list = new ArrayList<>();
        for(ShipList.Pair pair : this.SHIP_LIST){
            ShipCounterPair shipPair = new ShipCounterPair(pair.getNum(), pair.getSize());
            this.hashMapShipLabels.put(pair.getSize(), shipPair);
            HBoxExends hBox = createNewGuiShip(pair.getSize(), pair.getNum());
            list.add(hBox);
        }
        vBoxShips.getChildren().addAll(list);
    }

}
