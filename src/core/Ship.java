package core;

import core.communication_data.ShipID;
import core.communication_data.ShipPosition;

import java.io.Serializable;

public class Ship extends PlaygroundElement implements Serializable {

    private int lives;
    private ShipID id;
    private ShipPosition shipPosition;

    public enum LifeStatus {
        SUNKEN, ALIVE
    }

    private Ship(int lives, ShipID id, ShipPosition shipPosition){
        this.lives = lives;
        this.id = id;
        this.shipPosition = shipPosition;
    }

    /**
     * Constructs a ship with an auto generated ID and default position.
     *
     * @param length Length of the ship
     */
    Ship(int length){
        this(length, ShipID.getNextShipID(), ShipPosition.DEFAULT(length));
    }

    public LifeStatus getStatus() {
        if(this.lives == 0)
            return LifeStatus.SUNKEN;
        else
            return LifeStatus.ALIVE;
    }

    @Override
    public void gotHit() {
        super.gotHit();
        this.lives--;
    }

    public ShipID getId() {
        return id;
    }

    public ShipPosition getShipPosition(){
        return this.shipPosition;
    }

    public void setShipPosition(ShipPosition newPosition){
        this.shipPosition = newPosition;
    }
}
