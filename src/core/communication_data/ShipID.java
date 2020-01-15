package core.communication_data;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import core.utils.logging.LoggerLogic;

import java.beans.ConstructorProperties;
import java.io.Serializable;

@JsonIdentityInfo(generator= ObjectIdGenerators.IntSequenceGenerator.class, property="@id")
public class ShipID {

    private final int id;
    private static int nextID = 0;

    public ShipID() { // Jackson deserialization
        this.id = -1;
    }

    @ConstructorProperties({"id"})
    public ShipID(int id) {
        this.id = id;
    }

    /**
     * Jackson Constructor for deserialization
     */
    public ShipID(String strID) {
        id = Integer.parseInt(strID);
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

    @Override
    public String toString() {
        return "ShipID{" +
                "id=" + id +
                '}';
    }
}
