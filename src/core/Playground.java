package core;

import core.communication_data.PlaceShipResult;
import core.communication_data.ShipList;
import core.communication_data.ShipPosition;

public abstract class Playground {

    public enum FieldType{
        SHIP, WATER, FOG;
    }

    static class Field{
        boolean hit;
        FieldType type;
        PlaygroundElement element;

        public Field(FieldType type, PlaygroundElement element, boolean hit){
            this.hit = hit;
            this.type = type;
            this.element = element;
        }
    }

    /**
     * Positioning of the elements in playground:
     * 1. y vertical
     * 2. x horizontal
     * {@code Field f = this.elements[y][x];}
     */
    protected Field[][] elements;
    protected int size;

    public Playground(int size){
        this.size = size;
        this.elements = new Field[size][size];
    }

    protected void resetFields(FieldType type){

        for(int y = 0; y < this.size; y++){
            for(int x = 0; x < this.size; x++){
                PlaygroundElement element;
                if(type == FieldType.FOG)
                    element = new FogElement();
                else if(type == FieldType.WATER)
                    element = new WaterElement();
                else
                    assert false: "Cannot fill field with ships";
                this.elements[y][x] = new Field(type, new FogElement(), false);
            }
        }
    }

}
