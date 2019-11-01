package gui.ShipPlacement;

import gui.UiClasses.BattleShipGui;
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

        HBox hBox = createNewGuiShip("", 4);
        HBox hBox1 = createNewGuiShip("", 2);
        HBox hBox2 = createNewGuiShip("", 6);
        HBox hBox3 = createNewGuiShip("", 3);
        vBoxShips.getChildren().addAll(hBox, hBox1, hBox2, hBox3);

        preallocateFieldsWithWater();


    }


    private HBox createNewGuiShip(String IconPath, int numberOfShips) {

        HBox hBox = new HBox();
        hBox.setSpacing(20);
        hBox.setAlignment(Pos.CENTER);
        Label labelShipCounter = new Label("x " + numberOfShips);

        Image battleShipImage = new Image("/gui/ShipIcons/Testschiff.png");
        ImageView imageView = new ImageView(battleShipImage);
        addEventDragDetected(imageView);
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
     * @param imageView ShipImage which player will place on the playground
     */

    private void addEventDragDetected(ImageView imageView) {

        imageView.setOnDragDetected(mouseEvent -> {
            Dragboard dragboard = imageView.startDragAndDrop(TransferMode.ANY);

            ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putImage(imageView.getImage());
            dragboard.setContent(clipboardContent);
        });
    }

    /**
     * This Method makes it possible to move Ships
     *
     * @param imageView ShipImage which player will place on the playground
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
            Image ship = dragboard.getImage();

            int horizontalIndex = GridPane.getColumnIndex(imageView);
            int verticalIndex = GridPane.getRowIndex(imageView);
            Button button = new Button();

            //ToDO Bild kann derzeit noch nicht scallieren

            BackgroundImage backgroundImage = new BackgroundImage(new Image(getClass().getResource("/gui/ShipIcons/Testschiff.png").toExternalForm()), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
            Background background = new Background(backgroundImage);
            //button.setBackground(background);
            button.setStyle("-fx-background-color: #00ff00");

            addContextMenu(button);
            dataGridBattleship.add(button, horizontalIndex, verticalIndex, 3, 1);
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
                Node buttonShip = dataGridBattleship.getChildren().remove(index);
                dataGridBattleship.add(buttonShip, horizontalIndex, verticalIndex, 1,3);
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
