package core;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import core.communication_data.Position;
import core.communication_data.ShotResult;
import core.communication_data.TurnResult;
import core.playgrounds.PlaygroundEnemy;
import core.playgrounds.PlaygroundEnemyBuildUp;
import core.playgrounds.PlaygroundOwn;

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
    }

    public abstract Position makeTurn();



    public abstract boolean areAllShipsPlaced();

    /**
     * Enemy shot at this player
     *
     * @param position Position of the shot
     * @return A ShotResult with information about what was hit
     */
    public ShotResult gotHit(Position position) {
        return this.playgroundOwn.gotHit(position);
    }

    /**
     * This player shot at an enemy and got an result.
     * @param result  A ShotResult with information about what was hit
     */
    public void update(ShotResult result) {
        this.playgroundEnemy.update(result);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "name='" + name + '\'' +
                ", index=" + index +
                '}';
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

    public void setPlaygroundEnemy(PlaygroundEnemyBuildUp playgroundEnemy) {
        this.playgroundEnemy = playgroundEnemy;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
