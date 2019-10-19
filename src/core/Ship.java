package core;

import core.communication_data.ShipID;

public class Ship extends PlaygroundElement{

    private int lives;
    private ShipID id;

    private static int nextID = 0;

    public enum LifeStatus {
        SUNKEN, ALIVE
    }

    Ship(int lives, ShipID id){
        this.lives = lives;
        this.id = id;
    }

    Ship(int lives){
        this(lives, ShipID.getNextShipID());
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
}
