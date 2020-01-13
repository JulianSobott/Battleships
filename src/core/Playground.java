package core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonValue;
import core.communication_data.Position;
import core.communication_data.ShipList;
import core.utils.logging.LoggerLogic;
import org.junit.platform.commons.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
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

    public Playground() { // Jackson deserialization
    }

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

        private static Map<String, FieldType> namesMap = new HashMap<String, FieldType>(3);

        static {
            namesMap.put("ship", SHIP);
            namesMap.put("water", WATER);
            namesMap.put("fog", FOG);
        }

        @JsonCreator
        public static FieldType forValue(String value) {
            return namesMap.get(value.toLowerCase());
        }

        @JsonValue
        public String toValue() {
            for (Map.Entry<String, FieldType> entry : namesMap.entrySet()) {
                if (entry.getValue() == this)
                    return entry.getKey();
            }

            return null; // or fail
        }
    }
    public static class Field {

        private boolean hit;
        public FieldType type;
        public PlaygroundElement element;

        public Field() { // Jackson deserialization
        }

        public Field(FieldType type, PlaygroundElement element, boolean hit){
            this.hit = hit;
            this.type = type;
            this.element = element;
        }

        Field(FieldType type, PlaygroundElement element){
            this(type, element, false);
        }

        public static Field newWaterField(){
            return new Field(FieldType.WATER, new WaterElement(), false);
        }

        public void gotHit(){
            this.hit = true;
            this.element.gotHit();
        }

        public boolean isHit() {
            return hit;
        }

        public void setHit(boolean hit) {
            this.hit = hit;
        }

        public void setType(FieldType type) {
            this.type = type;
        }

        public void setElement(PlaygroundElement element) {
            this.element = element;
        }
    }
    /**
     * Call when a ship is sunken and water fields around can no longer be hit.
     *
     * @param waterFields All positions around the Ship where water is. All Positions exist and are on the playground
     */
    public void hitWaterFieldsAroundSunkenShip(Position[] waterFields){
        LoggerLogic.info("hitWaterFieldsAroundSunkenShip: waterFields= " + Arrays.toString(waterFields));
        for(Position position : waterFields){
            Field f = Field.newWaterField();
            f.gotHit();
            this.elements[position.getY()][position.getX()] = f;
        }
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

    public Field[][] getFields() {
        return this.elements;
    }

    public void setFields(Field[][] elements) {
        this.elements = elements;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
