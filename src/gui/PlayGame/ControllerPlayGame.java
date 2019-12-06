package gui.PlayGame;

import core.GameManager;
import core.Playground;
import core.communication_data.Position;
import core.communication_data.ShipPosition;
import core.communication_data.TurnResult;
import core.utils.logging.LoggerGUI;
import gui.UiClasses.BattleShipGui;
import gui.UiClasses.PaneExtends;
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

        PaneExtends p = new PaneExtends(PaneExtends.FieldType.FOG);
        p.setStyle("-fx-background-color: #2E64FE");
        gridPane.add(p, possHorizontal, possVertical);
        if (gridPane == this.gridPaneEnemyField) {
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

    private void addShipToPlayground(BattleShipGui battleShipGui) {

        if (battleShipGui.getPosition().getDirection() == ShipPosition.Direction.HORIZONTAL) {

            int index = battleShipGui.getPosition().getX() * playgroundSize + battleShipGui.getPosition().getY();
            for (int i = 0; i < battleShipGui.getPosition().getLength(); i++) {
                PaneExtends pane = (PaneExtends) gridPaneOwnField.getChildren().get(index);
                pane.setStyle("-fx-background-color: #00ff00");
                pane.setFieldType(PaneExtends.FieldType.SHIP);
                index += playgroundSize;
            }
        }
        if (battleShipGui.getPosition().getDirection() == ShipPosition.Direction.VERTICAL) {

            int index = battleShipGui.getPosition().getX() * playgroundSize + battleShipGui.getPosition().getY();
            for (int i = 0; i < battleShipGui.getPosition().getLength(); i++) {

                PaneExtends pane = (PaneExtends) gridPaneOwnField.getChildren().get(index);
                pane.setStyle("-fx-background-color: #00ff00");
                pane.setFieldType(PaneExtends.FieldType.SHIP);
                index++;

            }
        }
    }


    /**
     * ############################################# Turn #######################################################
     */

    private void addClickFieldEvent(PaneExtends p) {
        p.setOnMouseClicked(mouseEvent -> {
            int col = GridPane.getColumnIndex(p);
            int row = GridPane.getRowIndex(p);
            Position pos = new Position(col, row);
            TurnResult res = this.gameManager.shootP1(pos);
            if (res.getError() == TurnResult.Error.NONE) {
                if (res.getSHOT_RESULT().getType() == Playground.FieldType.SHIP) {
                    p.setStyle("-fx-background-color: #ff0000");
                    p.setFieldType(PaneExtends.FieldType.SHIP);
                } else if (res.getSHOT_RESULT().getType() == Playground.FieldType.WATER) {
                    p.setStyle("-fx-background-color: #ffff00");
                    p.setFieldType(PaneExtends.FieldType.WATER);
                } else if (false) {
                    //TODO: Fehlende information, dass Schiff gesunken is
                    updateGuiShipIsSunken(pos, p, gridPaneEnemyField);
                }
            } else {
                // TODO: show to user
                LoggerGUI.warning("Show this message to the user: " + res);
            }
            if (!res.isTURN_AGAIN() && !res.isFINISHED()) {
                this.getEnemyTurns();
            }
        });
    }

    private void getEnemyTurns() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                TurnResult res;
                do {
                    res = gameManager.getTurnPlayer2();
                    LoggerGUI.info("getTurnPlayer2 result: " + res);
                    if (res.getError() == TurnResult.Error.NONE) {
                        Position position = res.getSHOT_RESULT().getPosition();
                        int index = position.getX() * playgroundSize + position.getY();
                        PaneExtends p = (PaneExtends) gridPaneOwnField.getChildren().get(index);
                        if (res.getSHOT_RESULT().getType() == Playground.FieldType.SHIP) {
                            p.setStyle("-fx-background-color: #ff0000");
                            p.setFieldType(PaneExtends.FieldType.SHIP);
                        } else if (res.getSHOT_RESULT().getType() == Playground.FieldType.WATER) {
                            p.setStyle("-fx-background-color: #ffff00");
                            p.setFieldType(PaneExtends.FieldType.SHIP);
                        }
                    }
                } while (res.isTURN_AGAIN());
            }
        }).start();
    }


    /**
     * This method changes all fields around the sunken ships to the status "water".
     */

    //TODO: Es wird die Information benötigt, dass das Schiff versenkt wurde
    private void updateGuiShipIsSunken(Position position, PaneExtends paneExtends, GridPane gridPane) {

        String alignment = "failure";
        int index = position.getX() * playgroundSize + position.getY();
        int startIndex = -1;

        PaneExtends paneTestTop = (PaneExtends) gridPane.getChildren().get(index - 1);
        PaneExtends paneTestDown = (PaneExtends) gridPane.getChildren().get(index + 1);
        if (paneTestTop.getFieldType().equals(PaneExtends.FieldType.SHIP) || paneTestDown.getFieldType().equals(PaneExtends.FieldType.SHIP)) {
            alignment = "Vertical";
        } else {
            alignment = "Horizontal";
        }

        if (alignment.equals("Vertical")){

            startIndex = index;
            PaneExtends paneExtendsFindMinIndex = (PaneExtends) gridPane.getChildren().get(index);
            while ( paneExtendsFindMinIndex.getFieldType().equals(PaneExtends.FieldType.SHIP) )
            {
                //TODO : startindex berechnen + verfahren zum füllen der Felder um Schiff entwickeln..
                startIndex--;
            }
        }

    }


    /** ##########################################   Window Navigation  ############################################## */

    /**
     * go back to previous Scene
     */

    //TODO wo soll der Spieler beim Spielabruch landen ?? Wieder im Hauptmenü??
    public void goBackToShipPlacement() {

    }


}