package core;

import core.communication_data.ShipPosition;

public class Ship extends PlaygroundElement{

    private int lives;
    private int id;

    private ShipPosition shipPosition;

    public enum LifeStatus {
        SUNKEN, ALIVE
    }

    Ship(int lives, int id, ShipPosition shipPosition){
        this.lives = lives;
        this.id = id;
        this.shipPosition = shipPosition;
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

    public ShipPosition getShipPosition(){
        return this.shipPosition;
    }
}
