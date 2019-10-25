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

    Ship(int lives, ShipID id, ShipPosition shipPosition){
        this.lives = lives;
        this.id = id;
        this.shipPosition = shipPosition;
    }

    Ship(int lives, ShipPosition shipPosition){
        this(lives, ShipID.getNextShipID(), shipPosition);
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
