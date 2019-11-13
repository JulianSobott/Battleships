package core.communication_data;

import java.io.Serializable;

public class ShipID implements Serializable {

    private final int id;
    private static int nextID = 0;

    public ShipID(int id) {
        this.id = id;
    }

    public static ShipID getNextShipID(){
        return new ShipID(ShipID.nextID++);
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ShipID shipID = (ShipID) o;

        return id == shipID.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
