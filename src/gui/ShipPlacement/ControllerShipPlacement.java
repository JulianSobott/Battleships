package gui.ShipPlacement;

import gui.UiClasses.BattleShipGui;
import gui.UiClasses.ButtonShip;
import gui.UiClasses.HBoxExends;
import gui.UiClasses.ShipAlignment;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ControllerShipPlacement implements Initializable {

    @FXML
    private GridPane dataGridBattleship;

    @FXML
    private VBox vBoxShips;

    @FXML
    private Button buttonBack;


    private Label labelShipCounterBattleshipX5 = new Label();
    private Label labelShipCounterBattleshipX4 = new Label();
    private Label labelShipCounterBattleshipX3 = new Label();
    private Label labelShipCounterBattleshipX2 = new Label();
    private Label labelShipCounterBattleshipX1 = new Label();

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


    private static final int fieldSize = 5;
    private static final String filepathBackNewGame = "../newGame/GameType.fxml";
    private static final String filepathPlayground = "../";


    private ArrayList availableShips = new ArrayList();

    private ArrayList<Label> availableShipTypes = new ArrayList(); // Absteigend nach größe sortiert

    private ArrayList<BattleShipGui> battleShips = new ArrayList();


    public ControllerShipPlacement() {


    }

    /**
     * Gui is dynamically generated in the init method and adapted according to the specifications.
     */

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        HBoxExends hBox = createNewGuiShip(4, 1);
        HBoxExends hBox2 = createNewGuiShip(3, 3);
        HBoxExends hBox1 = createNewGuiShip(2, 2);
        HBoxExends hBox3 = createNewGuiShip(1, 2);

        vBoxShips.getChildren().addAll(hBox, hBox2, hBox1, hBox3);

        preallocateFieldsWithWater();


    }

    /**
     * generates the available graphical ship elements for the GUI.
     *
     * @param shipSize      length of the ship type
     * @param numberOfShips number of ships
     */

    private HBoxExends createNewGuiShip(int shipSize, int numberOfShips) {

        BattleShipGui battleShipGui = new BattleShipGui(shipSize, ShipAlignment.Horizontal);
        HBoxExends hBox = new HBoxExends(battleShipGui);
        hBox.setSpacing(20);
        hBox.setAlignment(Pos.CENTER);

        Label labelShipCounter = null;

        switch (shipSize) {
            case 5:
                labelShipCounter = new Label(numberOfShips + " x " + " 5-er Schiff");
                labelShipCounterBattleshipX5 = labelShipCounter;
                break;
            case 4:
                labelShipCounter = new Label(numberOfShips + " x " + " 4-er Schiff");
                labelShipCounterBattleshipX4 = labelShipCounter;
                break;
            case 3:
                labelShipCounter = new Label(numberOfShips + " x " + " 3-er Schiff");
                labelShipCounterBattleshipX3 = labelShipCounter;
                break;
            case 2:
                labelShipCounter = new Label(numberOfShips + " x " + " 2-er Schiff");
                labelShipCounterBattleshipX2 = labelShipCounter;
                break;
            case 1:
                labelShipCounter = new Label(numberOfShips + " x " + " 1-er Schiff");
                labelShipCounterBattleshipX1 = labelShipCounter;
                break;
        }

        Image battleShipImage = new Image("/gui/ShipIcons/Testschiff.png");
        ImageView imageView = new ImageView(battleShipImage);
        addEventDragDetected(hBox);
        imageView.setFitWidth(140);
        imageView.setFitHeight(60);

        hBox.getChildren().addAll(imageView, labelShipCounter);
        return hBox;
    }

    /**
     * Gui is dynamically generated in the init method and adapted according to the specifications.
     */

    private void generateWater(int possHorizontal, int possVertical) {

        Image battleShipImage = new Image("/gui/ShipIcons/Wasser_Groß.jpg");
        ImageView imageView = new ImageView(battleShipImage);
        imageView.setPreserveRatio(true);
        handleDragOver(imageView);
        handleDrop(imageView);

        imageView.fitWidthProperty().bind(buttonBack.widthProperty());
        dataGridBattleship.add(imageView, possHorizontal, possVertical);
    }


    /**
     * Water is pre-populated on the playing fields
     */

    private void preallocateFieldsWithWater() {

        for (int i = 0; i < fieldSize; i++) {
            for (int j = 0; j < fieldSize; j++) {
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

    private void handleDragOver(ImageView imageView) {

        imageView.setOnDragOver(dragEvent -> dragEvent.acceptTransferModes(TransferMode.ANY));
    }

    /**
     * This Method makes it possible to move Ships
     *
     * @param imageView ShipImage which player will place on the playground
     */

    private void handleDrop(ImageView imageView) {

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

            dataGridBattleship.add(button, horizontalIndex, verticalIndex, battleShipGui.getShipSize(), 1);
            button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);


            switch (battleShipGui.getShipSize()) {
                case 5:
                    numberOfBoningShipsX5Counter--;
                    labelShipCounterBattleshipX5.setText(numberOfBoningShipsX5Counter + numberOfBoningShipsX5);
                    break;
                case 4:
                    numberOfBoningShipsX4Counter--;
                    labelShipCounterBattleshipX4.setText(numberOfBoningShipsX4Counter + numberOfBoningShipsX4);
                    break;
                case 3:
                    numberOfBoningShipsX3Counter--;
                    labelShipCounterBattleshipX3.setText(numberOfBoningShipsX3Counter + numberOfBoningShipsX3);
                    break;
                case 2:
                    numberOfBoningShipsX2Counter--;
                    labelShipCounterBattleshipX2.setText(numberOfBoningShipsX2Counter + numberOfBoningShipsX2);
                    break;
                case 1:
                    numberOfBoningShipsX1Counter--;
                    labelShipCounterBattleshipX1.setText(numberOfBoningShipsX1Counter + numberOfBoningShipsX1);
                    break;
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
                Node nodeShip = dataGridBattleship.getChildren().remove(index);
                ButtonShip buttonShip = null;
                if (nodeShip instanceof ButtonShip) {
                    buttonShip = (ButtonShip) nodeShip;
                }

                switch (buttonShip.getBattleShipGui().getShipSize()) {
                    case 5:
                        numberOfBoningShipsX5Counter++;
                        labelShipCounterBattleshipX5.setText(numberOfBoningShipsX5Counter + numberOfBoningShipsX5);
                        break;
                    case 4:
                        numberOfBoningShipsX4Counter++;
                        labelShipCounterBattleshipX4.setText(numberOfBoningShipsX4Counter + numberOfBoningShipsX4);
                        break;
                    case 3:
                        numberOfBoningShipsX3Counter++;
                        labelShipCounterBattleshipX3.setText(numberOfBoningShipsX3Counter + numberOfBoningShipsX3);
                        break;
                    case 2:
                        numberOfBoningShipsX2Counter++;
                        labelShipCounterBattleshipX2.setText(numberOfBoningShipsX2Counter + numberOfBoningShipsX2);
                        break;
                    case 1:
                        numberOfBoningShipsX1Counter++;
                        labelShipCounterBattleshipX1.setText(numberOfBoningShipsX1Counter + numberOfBoningShipsX1);
                        break;
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
                Node node = dataGridBattleship.getChildren().remove(index);

                ButtonShip buttonShip = null;
                if (node instanceof ButtonShip) {
                    buttonShip = (ButtonShip) node;
                }
                BattleShipGui battleShipGui = buttonShip.getBattleShipGui();

                if (battleShipGui.getShipAlignment().equals(ShipAlignment.Horizontal)) {

                    dataGridBattleship.add(node, horizontalIndex, verticalIndex, 1, battleShipGui.getShipSize());
                    battleShipGui.setShipAlignment(ShipAlignment.Vertical);
                } else {

                    dataGridBattleship.add(node, horizontalIndex, verticalIndex, battleShipGui.getShipSize(), 1);
                    battleShipGui.setShipAlignment(ShipAlignment.Horizontal);
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

}
