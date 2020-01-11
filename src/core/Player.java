package core;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import core.communication_data.*;

@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
@JsonIdentityInfo(generator= ObjectIdGenerators.IntSequenceGenerator.class, property="@id")
public abstract class Player {

    protected PlaygroundOwn playgroundOwn;
    protected PlaygroundEnemy playgroundEnemy;

    protected String name;
    private int index;
    private int playgroundSize;

    public Player() { // Jackson deserialization
    }

    public Player(int index, String name, int playgroundSize) {
        this.index = index;
        this.name = name;
        this.playgroundSize = playgroundSize;
        this.playgroundOwn = new PlaygroundOwn(playgroundSize);
        this.playgroundEnemy = new PlaygroundEnemy(playgroundSize);
    }

    public abstract Position makeTurn();

    public PlaceShipResult placeShip(ShipPosition position) {
        PlaceShipResult res = this.playgroundOwn.placeShip(position);
        this.playgroundOwn.printField();
        return res;
    }

    public PlaceShipResult moveShip(ShipID id, ShipPosition position) {
        return this.playgroundOwn.moveShip(id, position);
    }

    public boolean deleteShip(ShipID id) {
        boolean res = this.playgroundOwn.deleteShip(id);
        this.playgroundOwn.printField();
        return res;
    }

    public abstract boolean areAllShipsPlaced();

    public ShotResult gotHit(Position position) {
        return this.playgroundOwn.gotHit(position);
    }

    public void update(ShotResult result) {
        this.playgroundEnemy.updateField(result.getPosition(), result.getType());
        if(result.getType() == Playground.FieldType.SHIP){
            ShotResultShip resultShip = (ShotResultShip)result;
            if(resultShip.getStatus() == Ship.LifeStatus.SUNKEN){
                this.playgroundEnemy.hitWaterFieldsAroundSunkenShip(resultShip.getWaterFields());
            }
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "name='" + name + '\'' +
                ", index=" + index +
                '}';
    }

    public PlaceShipsRandomRes placeShipsRandom() {
        return this.playgroundOwn.placeShipsRandom();
    }

    public boolean allEnemyShipsSunken() {
        return this.playgroundEnemy.areAllShipsSunken();
    }

    TurnResult.Error canShootAt(Position position) {
        return this.playgroundEnemy.canShootAt(position);
    }

    public int getIndex() {
        return this.index;
    }

    public PlaygroundOwn getPlaygroundOwn() {
        return playgroundOwn;
    }

    public PlaygroundEnemy getPlaygroundEnemy() {
        return playgroundEnemy;
    }

    public String getName() {
        return name;
    }

    public void setPlaygroundOwn(PlaygroundOwn playgroundOwn) {
        this.playgroundOwn = playgroundOwn;
    }

    public void setPlaygroundEnemy(PlaygroundEnemy playgroundEnemy) {
        this.playgroundEnemy = playgroundEnemy;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
