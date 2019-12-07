package core;

import core.communication_data.Position;
import core.communication_data.ShipList;
import core.utils.logging.LoggerLogic;

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

    public enum FieldType{
        SHIP, WATER, FOG;
    }

    public static class Field {
        private boolean hit;
        FieldType type;
        PlaygroundElement element;

        public Field(FieldType type, PlaygroundElement element, boolean hit){
            this.hit = hit;
            this.type = type;
            this.element = element;
        }

        Field(FieldType type, PlaygroundElement element){
            this(type, element, false);
        }

        static Field newWaterField(){
            return new Field(FieldType.WATER, new WaterElement(), false);
        }

        void gotHit(){
            this.hit = true;
            this.element.gotHit();
        }

        public boolean isHit() {
            return hit;
        }
    }

    /**
     * Call when a ship is sunken and water fields around can no longer be hit.
     *
     * @param waterFields All positions around the Ship where water is. All Positions exist and are on the playground
     */
    public void hitWaterFieldsAroundSunkenShip(Position[] waterFields){
        for(Position position : waterFields){
            Field f = Field.newWaterField();
            f.gotHit();
            this.elements[position.getY()][position.getX()] = f;
        }
        this.printField();
    }

    public int getSize() {
        return size;
    }

    public void printField(){
        StringBuilder s = new StringBuilder();
        s.append("\n");
        for(Field[] row : this.elements){
            for(Field f : row){
                if(f == null){
                    s.append("N");
                }
                else if(f.type == FieldType.SHIP){
                    s.append("S");
                }
                else if(f.type == FieldType.WATER){
                    s.append("~");
                }else if(f.type == FieldType.FOG){
                    s.append("=");
                }
            }
            s.append("\n");
        }
        LoggerLogic.debug(s.toString());
    }
}
