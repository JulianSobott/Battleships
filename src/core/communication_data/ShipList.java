package core.communication_data;

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
        // TODO: Replace with real implementation
        return new ShipList(new HashMap<>(){{
            for (int i = 1; i <= size; i++) {
                put(i, 5);
            }
        }});
    }

    public HashMap<Integer, Integer> getShips(){
        return this.ships;
    }

}
