package core;

import com.fasterxml.jackson.annotation.*;
import core.communication_data.Position;
import core.communication_data.ShipID;
import core.communication_data.ShipPosition;
import core.playgrounds.PlaygroundElement;
import core.playgrounds.PlaygroundEnemyBuildUp;

import java.io.Serializable;

public class Ship extends PlaygroundElement implements Serializable {

    private int lives;
    private ShipID id;
    private ShipPosition shipPosition;

    public enum LifeStatus {
        SUNKEN, ALIVE;
    }

    public Ship() {
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

    /**
     * Constructor for method {@link PlaygroundEnemyBuildUp#updateShipObjects(Position)}}.
     * Needed, when ships are only created, when they were hit.
     * @param position
     */
    public Ship(Position position) {
        this(0, ShipID.getNextShipID(), new ShipPosition(position.getX(), position.getY(),
                ShipPosition.Direction.HORIZONTAL, 1));
    }

    /**
     * Needed, when ships are only created, when they were hit.
     * @param shipPosition
     */
    Ship(ShipPosition shipPosition) {
        this(0, ShipID.getNextShipID(), shipPosition);
    }

    /**
     * Concatenate two adjacent ship objects to one Object
     * On this only the position is updated. s1 is not changed.
     * @param s1 A Ship Object adjacent to this
     */
    public void concatenateShips(Ship s1) {
        Ship s2 = this;
        ShipPosition.Direction newDirection;
        Position newPosition;
        int newLength;
        if (s1.getShipPosition().getX() != s2.getShipPosition().getX()) {
            // Ships horizontal
            assert s1.getShipPosition().getY() == s2.getShipPosition().getY(): "Ship seems to be placed vertically. " +
                    "Error in algorithm!";
            newDirection = ShipPosition.Direction.HORIZONTAL;
            Ship leftShip;
            if (s1.getShipPosition().getX() < s2.getShipPosition().getX()) {
                leftShip = s1;
            } else {
                leftShip = s2;
            }
            newPosition = new Position(leftShip.getShipPosition().getX(), leftShip.getShipPosition().getY());
        } else {
            // Ships vertical
            assert s1.getShipPosition().getX() == s2.getShipPosition().getX(): "Ship seems to be placed vertically. " +
                    "Error in algorithm!";
            newDirection = ShipPosition.Direction.VERTICAL;
            Ship topShip;
            if (s1.getShipPosition().getY() < s2.getShipPosition().getY()) {
                topShip = s1;
            } else {
                topShip = s2;
            }
            newPosition = new Position(topShip.getShipPosition().getX(), topShip.getShipPosition().getY());
        }
        newLength = s1.getShipPosition().getLength() + s2.getShipPosition().getLength();
        s2.setShipPosition(new ShipPosition(newPosition.getX(), newPosition.getY(), newDirection, newLength));
    }

    @JsonIgnore
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

    public int getLives() {
        return lives;
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

    public void setLives(int lives) {
        this.lives = lives;
    }

    public void setId(ShipID id) {
        this.id = id;
    }
}
