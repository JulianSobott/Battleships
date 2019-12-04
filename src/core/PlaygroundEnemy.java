package core;

import core.communication_data.Position;
import core.communication_data.TurnResult;
import core.utils.logging.LoggerLogic;

public class PlaygroundEnemy extends Playground{


    public PlaygroundEnemy(int size) {
        super(size);
        this.resetFields(FieldType.FOG);
    }

    public void updateField(Position position, FieldType type){
        PlaygroundElement element;
        if(type == FieldType.SHIP){
            element = this.getShipAtPosition(position.getX(), position.getY());
        }else {
            element = new WaterElement();
        }
        this.elements[position.getY()][position.getX()] = new Field(type, element, true);
    }

    private Ship getShipAtPosition(int x, int y){
        return null;
    }

    public boolean areAllShipsSunken(){
        // TODO: Implement
        return false;
    }

    public TurnResult.Error canShootAt(Position position) {
        this.printField();
        if (position.isOutsideOfPlayground(this.size))
            return TurnResult.Error.NOT_ON_PLAYGROUND;
        if (this.elements[position.getY()][position.getX()].type != FieldType.FOG)
            return TurnResult.Error.FIELD_ALREADY_DISCOVERED;
        return TurnResult.Error.NONE;
    }

}
