package gui.ShipPlacement;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
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



    public  ControllerShipPlacement(){


    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }


    private HBox createNewGuiShip(String IconPath, int numberOfShips){

        HBox hBox = new HBox();
        hBox.setSpacing(20);
        Label labelShipCounter = new Label( "x " + numberOfShips );

        return null;
    }


}
