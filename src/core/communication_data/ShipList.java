package core.communication_data;

import core.utils.CsvParser;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ShipList implements Iterable<ShipList.Pair>{

    @Override
    public Iterator<Pair> iterator() {
        return new Iterator<Pair>() {
            Iterator<Map.Entry<Integer, Integer>> i = ships.entrySet().iterator();

            @Override
            public boolean hasNext() {
                return i.hasNext();
            }

            @Override
            public Pair next() {
                Map.Entry<Integer, Integer> o = i.next();
                return new Pair(o.getKey(), o.getValue());
            }
        };
    }

    public static class Pair{
        private int num, size;

        Pair(int size, int num) {
            this.size = size;
            this.num = num;
        }

        public int getSize() {
            return size;
        }

        public int getNum() {
            return num;
        }

        @Override
        public String toString() {
            return "Pair{" +
                    "num=" + num +
                    ", size=" + size +
                    '}';
        }
    }

    // LENGTH, NUMBER
    private HashMap<Integer, Integer> ships;

    public ShipList(HashMap<Integer, Integer> ships) {
        this.ships = ships;
    }

    public static ShipList fromSize(int size){
        int[] shipArray = CsvParser.readCSVships(size);

        HashMap<Integer, Integer> shiphm = new HashMap<Integer, Integer>();
        for(int i =0; i<shipArray.length;i++){
            shiphm.put(5-i, shipArray[i]);
        }
        return new ShipList(shiphm);
    }

    public HashMap<Integer, Integer> getShips(){
        return this.ships;
    }

    public int getTotalNumberOfShips(){
        int sum = 0;
        for(Pair p : this){
            sum += p.num;
        }
        return sum;
    }

    public void decreaseNumShips(int length) {
        this.ships.replace(length, this.ships.get(length) - 1);
    }

}
