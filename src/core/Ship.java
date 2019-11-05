package core;

import core.communication_data.ShipID;
import core.communication_data.ShipPosition;

public class Ship extends PlaygroundElement{

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
        this(length, ShipID.getNextShipID(), new ShipPosition(0, 0, ShipPosition.Direction.HORIZONTAL, length));
    }

    LifeStatus getStatus() {
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

    ShipID getId() {
        return id;
    }

    ShipPosition getShipPosition(){
        return this.shipPosition;
    }

    public void setShipPosition(ShipPosition newPosition){
        this.shipPosition = newPosition;
    }
}
