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
import java.util.ResourceBundle;

public class ControllerShipPlacement implements Initializable {

    @FXML
    private GridPane dataGridBattleship;

    @FXML
    private VBox vBoxShips;

    @FXML
    private Button buttonBack;


    // ToDO Woher weiß man welcher Schiff länge gesetzt wurde ...

    private static final int fieldSize = 5;
    private static final String filepathBackNewGame = "../newGame/GameType.fxml";
    private static final String filepathPlayground = "../";


    private ArrayList availableShips = new ArrayList();

    private ArrayList<BattleShipGui> battleShips = new ArrayList();


    public ControllerShipPlacement() {


    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        HBoxExends hBox = createNewGuiShip(4, 1);
        HBoxExends hBox1 = createNewGuiShip(2, 2);

        vBoxShips.getChildren().addAll(hBox, hBox1);

        preallocateFieldsWithWater();


    }


    private HBoxExends createNewGuiShip(int shipSize, int numberOfShips) {

        BattleShipGui battleShipGui = new BattleShipGui(shipSize, ShipAlignment.Horizontal);
        HBoxExends hBox = new HBoxExends(battleShipGui);
        hBox.setSpacing(20);
        hBox.setAlignment(Pos.CENTER);
        Label labelShipCounter = new Label("x " + numberOfShips);

        Image battleShipImage = new Image("/gui/ShipIcons/Testschiff.png");
        ImageView imageView = new ImageView(battleShipImage);
        addEventDragDetected(hBox);
        imageView.setFitWidth(100);
        imageView.setFitHeight(50);

        hBox.getChildren().addAll(imageView, labelShipCounter);
        return hBox;
    }


    private ImageView generateEmtyImageView() {

        ImageView imageView = new ImageView();
        return imageView;
    }


    private void generateWater(int possHorizontal, int possVertical) {

        Image battleShipImage = new Image("/gui/ShipIcons/Wellen.jpg");
        ImageView imageView = new ImageView(battleShipImage);
        imageView.setFitWidth(120);
        imageView.setFitHeight(90);
        imageView.setPreserveRatio(true);
        handleDragOver(imageView);
        handleDrop(imageView);
        dataGridBattleship.add(imageView, possHorizontal, possVertical);

    }

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
                Node buttonShip = dataGridBattleship.getChildren().remove(index);

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
                }
                else {

                    dataGridBattleship.add(node, horizontalIndex, verticalIndex, battleShipGui.getShipSize(), 1 );
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
