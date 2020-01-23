package player;

import core.playgrounds.Playground;
import core.Ship;
import core.communication_data.*;
import core.utils.Random;
import core.utils.logging.LoggerLogic;
import core.utils.logging.LoggerState;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class PlaygroundHeatmap {

    private Playground.FieldType[][] final_fields;
    private Playground.FieldType[][] temp_fields;
    private List<ShipPosition> discoveredSunkenShips = new LinkedList<>();
    private final int SIZE;
    private ShipList shipList;
    private int finalNumShipsHit = 0;
    private int tempNumShipsHit = 0;


    // Dynamic programming
    private int[][] lastHeatMap;
    private Position lastPosition;
    private boolean rebuildMap = true;

    // Hyper parameters
    private int numPlacements = 200;
    private int numTriesSingleShip = 500;
    private int numTriesAllShips = 80;

    // statistics
    private int numCorrectGuesses = 0;
    private int numWrongGuesses = 0;


    @ConstructorProperties("size")
    public PlaygroundHeatmap(int size) {
        Random.random.setSeed(23);
        this.shipList = ShipList.fromSize(size);
        this.SIZE = size;
        this.final_fields = new Playground.FieldType[size][size];
        this.temp_fields = new Playground.FieldType[size][size];
        this.clearField(this.temp_fields);
        this.clearField(this.final_fields);
    }

    public void updateField(Position position, Playground.FieldType fieldType) {
        rebuildMap = true;
        if (fieldType == Playground.FieldType.SHIP) {
            this.finalNumShipsHit++;
            // rebuildMap = false;
        } else {
            if(lastHeatMap != null)
                this.lastHeatMap[position.getY()][position.getX()] = 0;
        }
        this.final_fields[position.getY()][position.getX()] = fieldType;
    }

    public int[][] buildHeatMap(int numIterations) {
        int[][] heatMap = new int[SIZE][SIZE];
        int numFails = 0;
        for (int i = 0; i < numIterations; i++) {
            boolean succeed = this.buildPossiblePlacements();
            if (succeed)
                this.addPossiblePlacementsToHeatMap(heatMap);
            else {
                numFails++;
            }
        }
        if(numFails == numIterations)
            return lastHeatMap;
        LoggerLogic.debug("Num of fails, where no ships could be placed: " + numFails);
        lastHeatMap = heatMap;
        return heatMap;
    }

    public Position getHottestPosition(int numIterations) {
        int[][] heatMap;
        if(rebuildMap) {
            numIterations = numPlacements;
            heatMap = buildHeatMap(numIterations);
        } else{
            heatMap = lastHeatMap;
        }
        int xMax = 0, yMax = 0, maxHeat = 0;
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                if(heatMap[y][x] > maxHeat && !this.isAlreadyDiscoveredShipAt(x, y)) {
                    xMax = x;
                    yMax = y;
                    maxHeat = heatMap[y][x];
                }
            }
        }
        if(maxHeat == 0) {
            assert false: "Should find solution:";
            return null;
        }
        lastPosition = new Position(xMax, yMax);
        return lastPosition;
    }

    private void addPossiblePlacementsToHeatMap(int[][] heatMap) {
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                if(this.temp_fields[y][x] == Playground.FieldType.SHIP)
                    heatMap[y][x]++;
            }
        }
    }

    private boolean buildPossiblePlacements() {
        for (int i = 0; i < numTriesAllShips; i++) {
            this.clearField(this.temp_fields);
            this.tempNumShipsHit = 0;
            this.placeAlreadySunkenShips();
            if (this.placeShipsRandom() && this.tempNumShipsHit == this.finalNumShipsHit) {
                return true;
            }
        }
        return false;
    }

    private void placeAlreadySunkenShips() {
        for (ShipPosition pos : this.discoveredSunkenShips) {
            for (Position p : pos.generateIndices()) {
                this.temp_fields[p.getY()][p.getX()] = Playground.FieldType.SHIP;
            }
            this.tempNumShipsHit += pos.getLength();
        }
    }

    private boolean placeShipsRandom() {
        //tempFreeFields.addAll(freeFields);

        for (ShipList.Pair pair : this.shipList) {
            for (int shipI = 0; shipI < pair.getNum(); shipI++) {
                if (!this.placeSingleShipRandom(pair.getSize())) {
                    //LoggerLogic.debug("No more solution for ship placement.");
                    return false;
                }
            }
        }
        return true;
    }

    private boolean placeSingleShipRandom(int shipLength) {
        for (int i = 0; i < numTriesSingleShip; i++) {
            int x = Random.random.nextInt(SIZE);
            int y = Random.random.nextInt(SIZE);

            ShipPosition.Direction dir = Random.random.nextBoolean() ? ShipPosition.Direction.HORIZONTAL :
                    ShipPosition.Direction.VERTICAL;
            ShipPosition position = new ShipPosition(x, y, dir, shipLength);
            if (!position.isOutsideOfPlayground(SIZE) && this.tryPlaceShip(position)) {
                return true;
            }
        }
        return false;
    }

    private boolean tryPlaceShip(ShipPosition position) {
        int[][] surroundingFields = {{-1, -1}, {-1, 0}, {0, -1}, {0, 0}, {0, 1}, {1, 0}, {1, 1}, {-1, 1}, {1, -1}};
        Position[] positions = position.generateIndices();
        List<Position> positionList = Arrays.asList(positions);
        int locNumHits = 0;
        for (Position p : positions) {
            // Not on Playground
            if (p.isOutsideOfPlayground(this.SIZE))
                return false;
            // Water
            if (this.final_fields[p.getY()][p.getX()] == Playground.FieldType.WATER)
                return false;
            // Covered a already hit ship
            if (this.final_fields[p.getY()][p.getX()] == Playground.FieldType.SHIP)
                locNumHits++;

            for (int[] surroundingField : surroundingFields) {
                // 0 <= x < size
                int x = Math.min(Math.max(p.getX() + surroundingField[0], 0), this.SIZE - 1);
                // 0 <= y < size
                int y = Math.min(Math.max(p.getY() + surroundingField[1], 0), this.SIZE - 1);

                // Ship on temp field
                if (this.temp_fields[y][x] == Playground.FieldType.SHIP)
                    return false;
                // Final Ship that is not covered by this position
                if (this.final_fields[y][x] == Playground.FieldType.SHIP) {
                    Position surFieldPos = new Position(x, y);
                    if (!positionList.contains(surFieldPos))
                        return false;
                }

            }
        }
        for (Position p : positions){
            this.temp_fields[p.getY()][p.getX()] = Playground.FieldType.SHIP;
        }
        this.tempNumShipsHit += locNumHits;
        return true;
    }

    private void clearField(Playground.FieldType[][] field) {
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                field[y][x] = Playground.FieldType.FOG;
            }
        }
    }

    public void printFields(){
        StringBuilder s = new StringBuilder();
        s.append("FINAL=0, TEMP=1, SHIP=S, WATER=~, FOG=#\n");
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                String[] final_temp = new String[2];
                int f = 0, t = 1;
                if(this.final_fields[y][x] == Playground.FieldType.SHIP){
                    final_temp[f] = "S";
                }
                if(this.temp_fields[y][x] == Playground.FieldType.SHIP){
                    final_temp[t] = "S";
                }
                if(this.final_fields[y][x] == Playground.FieldType.WATER){
                    final_temp[f] = "~";
                }
                if(this.temp_fields[y][x] == Playground.FieldType.WATER){
                    final_temp[t] = "~";
                }
                if(this.final_fields[y][x] == Playground.FieldType.FOG){
                    final_temp[f] = "#";
                }
                if(this.temp_fields[y][x] == Playground.FieldType.FOG){
                    final_temp[t] = "#";
                }
                s.append(final_temp[0]).append(final_temp[1]).append(" ");
            }
            s.append("\n");
        }
        LoggerLogic.debug(s.toString());
    }

    public boolean isAlreadyDiscoveredShipAt(int x, int y){
        return this.final_fields[y][x] == Playground.FieldType.SHIP;
    }

    public void update(ShotResult result) {
        this.updateField(result.getPosition(), result.getType());
        if(result instanceof ShotResultShip) {
            ShotResultShip resultShip = (ShotResultShip)result;
            if(resultShip.getStatus() == Ship.LifeStatus.SUNKEN) {
                for (Position waterPos : resultShip.getWaterFields()) {
                    this.updateField(waterPos, Playground.FieldType.WATER);
                }
                this.discoveredSunkenShips.add(resultShip.getShipPosition());
                this.shipList.decreaseNumShips(resultShip.getShipPosition().getLength());
            }
            numCorrectGuesses++;
        }else{
            numWrongGuesses++;
        }
        if(shipList.getTotalNumberOfShips() == 0) {
            printStatistics();
        }
    }

    public static void printHeatMap(int[][] map){
        StringBuilder s = new StringBuilder();
        s.append("\n");
        for(int[] row : map){
            for(int heat : row){
                s.append(String.format("%3d ", heat));
            }
            s.append("\n");
        }
        LoggerLogic.debug(s.toString());
    }

    public void printStatistics() {
        LoggerState.info("Statistics: avgScore="+ ((float)numCorrectGuesses)/numWrongGuesses+ ", " +
                "numCorrectGuesses=" + numCorrectGuesses +
                ", numWrongGuesses=" + numWrongGuesses);
    }


    public Playground.FieldType[][] getFinal_fields() {
        return final_fields;
    }

    public void setFinal_fields(Playground.FieldType[][] final_fields) {
        this.final_fields = final_fields;
    }

    public Playground.FieldType[][] getTemp_fields() {
        return temp_fields;
    }

    public void setTemp_fields(Playground.FieldType[][] temp_fields) {
        this.temp_fields = temp_fields;
    }

    public List<ShipPosition> getDiscoveredSunkenShips() {
        return discoveredSunkenShips;
    }

    public void setDiscoveredSunkenShips(List<ShipPosition> discoveredSunkenShips) {
        this.discoveredSunkenShips = discoveredSunkenShips;
    }

    public int getSIZE() {
        return SIZE;
    }

    public int getFinalNumShipsHit() {
        return finalNumShipsHit;
    }

    public void setFinalNumShipsHit(int finalNumShipsHit) {
        this.finalNumShipsHit = finalNumShipsHit;
    }

    public int getTempNumShipsHit() {
        return tempNumShipsHit;
    }

    public void setTempNumShipsHit(int tempNumShipsHit) {
        this.tempNumShipsHit = tempNumShipsHit;
    }
}
