package gui.UiClasses;

import javafx.scene.layout.HBox;

public class HBoxExends extends HBox {

    private BattleShipGui battleShipGui;

    public HBoxExends(BattleShipGui battleShipGui){

        this.battleShipGui = battleShipGui;
    }


    public BattleShipGui getBattleShipGui() {
        return battleShipGui;
    }

    public void setBattleShipGui(BattleShipGui battleShipGui) {
        this.battleShipGui = battleShipGui;
    }
}
