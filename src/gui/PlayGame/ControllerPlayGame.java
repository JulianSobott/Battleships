package gui.PlayGame;

import core.GameManager;
import core.Playground;
import core.communication_data.Position;
import core.communication_data.ShipPosition;
import core.communication_data.TurnResult;
import core.utils.Logger;
import gui.UiClasses.BattleShipGui;
import gui.UiClasses.HBoxExends;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
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
    GameManager gameManager;

    public ControllerPlayGame(int playgroudSize, ArrayList<BattleShipGui> shipPositionList, GameManager gameManager) {

        this.playgroundSize = playgroudSize;
        this.shipPositionList = shipPositionList;
        this.gameManager = gameManager;
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

        preallocateFieldsWithWater(gridPaneOwnField);
        preallocateFieldsWithWater(gridPaneEnemyField);

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
     * Water is pre-populated on the playing fields
     */

    private void preallocateFieldsWithWater(GridPane gridPane) {

        for (int i = 0; i < this.playgroundSize; i++) {
            for (int j = 0; j < this.playgroundSize; j++) {
                generateWater(i, j, gridPane);
            }
        }
    }


    /**
     * Gui is dynamically generated in the init method and adapted according to the specifications.
     */

    private void generateWater(int possHorizontal, int possVertical, GridPane gridPane) {

        Pane p = new Pane();
        p.setStyle("-fx-background-color: #2E64FE");
        gridPane.add(p, possHorizontal, possVertical);
        if(gridPane == this.gridPaneEnemyField) {
            this.addClickFieldEvent(p);
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

        if (battleShipGui.getPosition().getDirection() == ShipPosition.Direction.HORIZONTAL) {

            int index = battleShipGui.getPosition().getX() * playgroundSize + battleShipGui.getPosition().getY();
            for (int i = 0; i < battleShipGui.getPosition().getLength() ; i++) {
                Pane pane = (Pane) gridPaneOwnField.getChildren().get(index);
                pane.setStyle("-fx-background-color: #00ff00");
                index+= playgroundSize;
            }
        }
        if (battleShipGui.getPosition().getDirection() == ShipPosition.Direction.VERTICAL) {

          int index = battleShipGui.getPosition().getX() * playgroundSize + battleShipGui.getPosition().getY();
            for (int i = 0; i < battleShipGui.getPosition().getLength() ; i++) {

                Pane pane = (Pane) gridPaneOwnField.getChildren().get(index);
                pane.setStyle("-fx-background-color: #00ff00");
                index++;

            }
        }
    }



    /** ############################################# Turn ####################################################### */

    private void addClickFieldEvent(Pane p) {
        p.setOnMouseClicked(mouseEvent -> {
            int col = GridPane.getColumnIndex(p);
            int row = GridPane.getRowIndex(p);
            Position pos = new Position(col, row);
            TurnResult res = this.gameManager.shootP1(pos);
            Logger.debug(res);
            if(res.getError() == TurnResult.Error.NONE){
                if(res.getSHOT_RESULT().getType() == Playground.FieldType.SHIP){
                    p.setStyle("-fx-background-color: #ff0000");
                }else if(res.getSHOT_RESULT().getType() == Playground.FieldType.WATER){
                    p.setStyle("-fx-background-color: #ffff00");
                }
            }else{
                Logger.warning(res);
            }
            if(!res.isTURN_AGAIN() && !res.isFINISHED()){
                this.getEnemyTurn();
            }
        });
    }

    private void getEnemyTurn() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                TurnResult res = gameManager.getTurnPlayer2();
                Logger.debug("AI TURN res in GUI: ", res);
                if(res.getError() == TurnResult.Error.NONE){
                    Position position = res.getSHOT_RESULT().getPosition();
                    int index = position.getX() * playgroundSize + position.getY();
                    Pane p = (Pane)gridPaneOwnField.getChildren().get(index);
                    if(res.getSHOT_RESULT().getType() == Playground.FieldType.SHIP){
                        p.setStyle("-fx-background-color: #ff0000");
                    }else if(res.getSHOT_RESULT().getType() == Playground.FieldType.WATER){
                        p.setStyle("-fx-background-color: #ffff00");
                    }
                }
            }
        }).start();
    }

    /** ##########################################   Window Navigation  ############################################## */

    /**
     * go back to previous Scene
     */

    //TODO wo soll der Spieler beim Spielabruch landen ?? Wieder im Hauptmenü??
    public void goBackToShipPlacement() {

    }



}
