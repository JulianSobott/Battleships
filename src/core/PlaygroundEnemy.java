package core;

import core.communication_data.Position;

public class PlaygroundEnemy extends Playground{

    // TODO: is the ShipPool and Ship objects needed here? Or is everything done with fields

    public PlaygroundEnemy(int size) {
        super(size);
        this.resetFields(FieldType.FOG);
    }

    // TODO: java docs + check implementation
    public void updateField(Position position, FieldType type){
        PlaygroundElement element;
        if(type == FieldType.SHIP){
            element = this.getShipAtPosition(position.getX(), position.getY());
        }else {
            element = new WaterElement();
        }
        this.elements[position.getX()][position.getY()] = new Field(type, element, true);
    }

    private Ship getShipAtPosition(int x, int y){
        return null;
    }
}
