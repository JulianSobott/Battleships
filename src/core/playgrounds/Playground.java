package core.playgrounds;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import core.FogElement;
import core.Ship;
import core.WaterElement;
import core.communication_data.Position;
import core.communication_data.ShipID;
import core.communication_data.ShipList;
import core.serialization.ShipHashMapSerializer;
import core.utils.logging.LoggerLogic;

import java.util.*;

/**
 * Class with necessary implementations and attributes for every Playground
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, property="@class")
public abstract class Playground {

    /**
     * Positioning of the elements in playground:
     * 1. y vertical
     * 2. x horizontal
     * {@code Field f = this.elements[y][x];}
     */
    protected Field[][] elements;
    protected int size;
    protected int numShipsFields = 0;
    protected int numHitShipsFields = 0;

    @JsonSerialize(keyUsing = ShipHashMapSerializer.class)
    protected HashMap<ShipID, Ship> shipHashMap  = new HashMap<>();

    public Playground() { // Jackson deserialization
    }

    public Playground(int size){
        this.size = size;
        this.elements = new Field[size][size];
        ShipList l = ShipList.fromSize(size);
        for(ShipList.Pair p : l){
            this.numShipsFields += p.getSize() * p.getNum();
        }
    }

    /**
     * Reset all fields to type.
     *
     * @param type Type of every field
     */
    public void resetAll(FieldType type){
        this.resetFields(type);
    }

    /**
     * Equivalent of calling {@link #resetAll(FieldType)}
     *
     * @param type Type of every field
     */
    protected void resetFields(FieldType type){
        for(int y = 0; y < this.size; y++){
            for(int x = 0; x < this.size; x++){
                this.resetField(type, new Position(x, y));
            }
        }
    }

    /**
     * Resets only certain fields to the specified type.
     *
     * @param type All fields will have this type
     * @param positions All positions that shall have the new type
     */
    protected void resetFields(FieldType type, Position[] positions){
        for(Position pos : positions){
            this.resetField(type, pos);
        }
    }

    /**
     * Resets only one field to a new type.
     * @param type Type of the field. Only Water or Fog is allowed here.
     * @param pos Field position
     */
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

    /**
     *
     * @return true, when all ships are sunken
     */
    public boolean areAllShipsSunken() {
        return numShipsFields - numHitShipsFields == 0;
    }

    /**
     * Get a list of all Positions around a ship that are water.
     * When a ship is sunken it is known, that these fields are water.
     *
     * @param s A ship. Most likely a ship that is sunken.
     * @return All positions around this ship
     */
    public Position[] getSurroundingWaterPositions(Ship s) {
        HashSet<Position> waterPositions = new HashSet<Position>();
        int[][] surroundingFields = {{-1, -1}, {-1, 0}, {0, -1}, {0, 0}, {0, 1}, {1, 0}, {1, 1}, {-1, 1}, {1, -1}};
        for (Position shipPosition : s.getShipPosition().generateIndices()) {
            for (int[] surrPos : surroundingFields) {
                int x = surrPos[0] + shipPosition.getX();
                int y = surrPos[1] + shipPosition.getY();
                if(!(x < 0 || y < 0)){
                    Position pos = new Position(x, y);
                    if (!pos.isOutsideOfPlayground(this.size) && this.elements[y][x].type == FieldType.WATER) {
                        waterPositions.add(pos);
                    }
                }
            }
        }
        Position[] positions = new Position[waterPositions.size()];
        return waterPositions.toArray(positions);
    }

    /**
     * Add a ship object to the HashMap. Needs to be called every time a Ship is placed on the playground
     * @param ship New Ship on the Playground
     */
    protected void putShip(Ship ship) {
        assert ship != null && !shipHashMap.containsKey(ship.getId()): "Cannot put ship in HashMap. ship=" + ship;
        shipHashMap.put(ship.getId(), ship);
    }

    /**
     * Return the ship by the specified ID. The ship with this ID must be added before.
     * @param shipID ShipID of the wanted Ship
     * @return The Ship with the specified ID
     */
    protected Ship getShipByID(ShipID shipID) {
        assert shipID != null && !shipHashMap.containsKey(shipID): "ShipID is not in HashMap. id=" + shipID;
        return shipHashMap.get(shipID);
    }

    /**
     * Remove a Ship from the HashMap. Call when a ship is removed from the Playground.
     * @param shipID ShipID of the Ship
     * @return The Ship that was removed.
     */
    protected Ship removeShipByID(ShipID shipID) {
        assert shipID != null && shipHashMap.containsKey(shipID): "ShipID is not in HashMap. id=" + shipID;
        return shipHashMap.remove(shipID);
    }

    /**
     * Every field has a FieldType.
     * Some methods are needed to serialize it with jackson.
     */
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

    /**
     * Every field on a Playground is of type Field.
     * This class contains basic/general information about a field.
     * Some fields need more special information, which is in in then stored in the {@link #element}.
     */
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

        public Field(FieldType type, PlaygroundElement element){
            this(type, element, false);
        }

        /**
         * Factory class for more convenient generation of a simple water field.
         * @return A new, not hit WaterField
         */
        public static Field newWaterField(){
            return new Field(FieldType.WATER, new WaterElement(), false);
        }

        /**
         * Call when a shoot hit this field.
         */
        public void gotHit(){
            this.hit = true;
            this.element.gotHit();
        }

        public boolean isHit() {
            return hit;
        }

        // Setter needed for jackson

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

    @JsonIgnore
    public Ship[] getAllShips() {
        return new ArrayList<Ship>(shipHashMap.values()).toArray(new Ship[0]);
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

    public int getNumShipsFields() {
        return numShipsFields;
    }

    public void setNumShipsFields(int numShipsFields) {
        this.numShipsFields = numShipsFields;
    }

    public int getNumHitShipsFields() {
        return numHitShipsFields;
    }

    public void setNumHitShipsFields(int numHitShipsFields) {
        this.numHitShipsFields = numHitShipsFields;
    }

    public HashMap<ShipID, Ship> getShipHashMap() {
        return shipHashMap;
    }

    public void setShipHashMap(HashMap<ShipID, Ship> shipHashMap) {
        this.shipHashMap = shipHashMap;
    }
}
