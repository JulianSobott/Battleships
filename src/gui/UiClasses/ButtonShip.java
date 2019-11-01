package gui.UiClasses;

import javafx.scene.Node;
import javafx.scene.control.Button;

public class ButtonShip extends Button {

    private BattleShipGui battleShipGui;


    public ButtonShip(BattleShipGui battleShipGui) {
        this.battleShipGui = battleShipGui;
    }

    public ButtonShip(String s, BattleShipGui battleShipGui) {
        super(s);
        this.battleShipGui = battleShipGui;
    }

    public ButtonShip(String s, Node node, BattleShipGui battleShipGui) {
        super(s, node);
        this.battleShipGui = battleShipGui;
    }

    public BattleShipGui getBattleShipGui() {
        return battleShipGui;
    }

    public void setBattleShipGui(BattleShipGui battleShipGui) {
        this.battleShipGui = battleShipGui;
    }
}
