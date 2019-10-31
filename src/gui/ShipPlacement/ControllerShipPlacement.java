package gui.ShipPlacement;

import gui.UiClasses.BattleShipGui;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ControllerShipPlacement implements Initializable {

    @FXML
    private GridPane dataGridBattleship;

    @FXML
    private VBox vBoxShips;


    // ToDO Woher weiß man welcher Schiff länge gesetzt wurde ...

    private static final int fieldSize = 5;

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
        imageView.setFitWidth(140);
        imageView.setFitHeight(110);
        handleDragOver(imageView);
        handleDrop(imageView);
        dataGridBattleship.add(imageView, possHorizontal, possVertical);

    }

    private void preallocateFieldsWithWater(){

        for (int i= 0; i < fieldSize; i++)
        {
            for (int j= 0; j < fieldSize; j++)
            {
                generateWater(i,j);
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
            imageView.setImage(ship);

        });

    }


}
