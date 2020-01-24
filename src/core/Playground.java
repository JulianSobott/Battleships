package core;

import core.communication_data.Position;
import core.communication_data.ShipList;

public abstract class Playground {

    /**
     * Positioning of the elements in playground:
     * 1. y vertical
     * 2. x horizontal
     * {@code Field f = this.elements[y][x];}
     */
    protected Field[][] elements;
    protected int size;
    ShipPool shipPool;
    public Playground(int size){
        this.size = size;
        this.elements = new Field[size][size];
        this.shipPool = new ShipPool(ShipList.fromSize(size));
    }

    /**
     * Reset all fields to type.
     * Releases all ships. Same as creating a new Playground
     *
     * @param type
     */
    void resetAll(FieldType type){
        this.resetFields(type);
        this.shipPool.releaseAll();
    }

    protected void resetFields(FieldType type){
        for(int y = 0; y < this.size; y++){
            for(int x = 0; x < this.size; x++){
                this.resetField(type, new Position(x, y));
            }
        }
    }

    protected void resetFields(FieldType type, Position[] positions){
        for(Position pos : positions){
            this.resetField(type, pos);
        }
    }

    private void resetField(FieldType type, Position pos){
        PlaygroundElement element = new WaterElement();
        if(type == FieldType.FOG)
            element = new FogElement();
        else if(type == FieldType.WATER)
            element = new WaterElement();
        else
            assert false: "Cannot fill field with ships";
        this.elements[pos.getY()][pos.getX()] = new Field(type, element, false);
    }

    public Field[][] getElements() {
        return elements;
    }

    public enum FieldType{
        SHIP, WATER, FOG;
    }

    public static class Field {
        public boolean hit;
        public FieldType type;
        public PlaygroundElement element;

        public Field(FieldType type, PlaygroundElement element, boolean hit){
            this.hit = hit;
            this.type = type;
            this.element = element;
        }

        Field(FieldType type, PlaygroundElement element){
            this(type, element, false);
        }

        // TODO: Maybe add build factories: e.g. newWater(),
    }

}
