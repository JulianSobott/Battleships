package gui.PlayGame;

import core.communication_data.PlaceShipResult;
import core.communication_data.ShipPosition;
import gui.UiClasses.BattleShipGui;
import gui.UiClasses.ButtonShip;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ControllerPlayGame implements Initializable {

    @FXML
    public GridPane gridPaneOwnField;

    @FXML
    public GridPane gridPaneEnemyField;


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

    public ControllerPlayGame(int playgroudSize, ArrayList<BattleShipGui> shipPositionList) {

        this.playgroundSize = playgroudSize;
        this.shipPositionList = shipPositionList;
    }

    /**
     * #############################################   init methods  ################################################
     */


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

    private void placeOwnShipsOnOwnPlayground() {

        for (BattleShipGui battleShipGui : shipPositionList) {

            addShipToPlayground(battleShipGui);
        }

    }


    /**
     * The method places the graphic object on the surface.
     *
     * @param battleShipGui Object that holds all important information about the graphical object.
     */

    private void addShipToPlayground( BattleShipGui battleShipGui) {

        Button buttonShip = new Button();
        buttonShip.setStyle("-fx-background-color: #00ff00");

        if (battleShipGui.getPosition().getDirection() == ShipPosition.Direction.HORIZONTAL) {
            gridPaneOwnField.add(buttonShip, battleShipGui.getPosition().getX(), battleShipGui.getPosition().getY(), battleShipGui.getPosition().getLength(), 1);
        }
        if (battleShipGui.getPosition().getDirection() == ShipPosition.Direction.VERTICAL) {
            gridPaneOwnField.add(buttonShip, battleShipGui.getPosition().getX(), battleShipGui.getPosition().getY(), 1, battleShipGui.getPosition().getLength());
        }
        buttonShip.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    }


    /** ##########################################   Window Navigation  ############################################## */

    /**
     * go back to previous Scene
     */

    //TODO wo soll der Spieler beim Spielabruch landen ?? Wieder im Hauptmenü??
    public void goBackToShipPlacement() {


    }

}
