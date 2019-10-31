package gui.ShipPlacement;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerShipPlacement implements Initializable {

    @FXML
    private GridPane dataGridBattleship;

    @FXML
    private VBox vBoxShips;


    public ControllerShipPlacement() {


    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        HBox hBox = createNewGuiShip("", 4);
        HBox hBox1 = createNewGuiShip("", 2);
        HBox hBox2 = createNewGuiShip("", 6);
        HBox hBox3 = createNewGuiShip("", 3);
        vBoxShips.getChildren().addAll(hBox, hBox1, hBox2,hBox3);

    }


    private HBox createNewGuiShip(String IconPath, int numberOfShips) {

        HBox hBox = new HBox();
        hBox.setSpacing(20);
        hBox.setAlignment(Pos.CENTER);
        Label labelShipCounter = new Label("x " + numberOfShips);

        Image imageShip = new Image("/gui/ShipIcons/Testschiff.png");
        ImageView imageView = new ImageView(imageShip);
        imageView.setFitWidth(100);
        imageView.setFitHeight(50);

        hBox.getChildren().addAll(imageView, labelShipCounter);
        return hBox;
    }


}
