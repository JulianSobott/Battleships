package gui.PlayGame;

import gui.UiClasses.BattleShipGui;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ControllerPlayGame  implements Initializable {

    @FXML
    public GridPane gridPaneOwnField;

    @FXML
    public  GridPane gridPaneEnemyField;


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

    public ControllerPlayGame(int playgroudSize, ArrayList<BattleShipGui> shipPositionList){

        this.playgroundSize = playgroudSize;
        this.shipPositionList = shipPositionList;
    }

    /** #############################################   init methods  ################################################ */


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        HBox.setHgrow(vBoxOwnPlayground, Priority.ALWAYS);
        HBox.setHgrow(vBoxEnemyPlayground, Priority.ALWAYS);

        VBox.setVgrow(vBoxOwnPlayground, Priority.ALWAYS);
        VBox.setVgrow(vBoxEnemyPlayground, Priority.ALWAYS);

        generateGridPane(gridPaneOwnField);
        generateGridPane(gridPaneEnemyField);

        placeOwnShipsOnOwnPlayground();

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
     * place own ships on own Playground in the GUI
     */

    private void placeOwnShipsOnOwnPlayground(){

        for (BattleShipGui battleShipGui: shipPositionList) {

            generateGuiShip(battleShipGui);
        }

    }

    /**
     * generates a Gui Ship for Gui
     */

    private void generateGuiShip(BattleShipGui battleShipGui){


    }



    /** ##########################################   Window Navigation  ############################################## */

    /**
     * go back to previous Scene
     */

    //TODO wo soll der Spieler beim Spielabruch landen ?? Wieder im Hauptmenü??
    public void goBackToShipPlacement() {


    }

}
