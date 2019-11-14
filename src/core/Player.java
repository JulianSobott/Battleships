package core;

import core.communication_data.*;

public abstract class Player {

    private PlaygroundOwn playgroundOwn;
    private PlaygroundEnemy playgroundEnemy;

    private String name;

    public Player(String name, int playgroundSize) {
        this.name = name;
        this.playgroundOwn = new PlaygroundOwn(playgroundSize);
        this.playgroundEnemy = new PlaygroundEnemy(playgroundSize);
    }

    public abstract TurnResult makeTurn();

    public PlaceShipResult placeShip(ShipPosition position){
        return this.playgroundOwn.placeShip(position);
    }
    public PlaceShipResult moveShip(ShipID id, ShipPosition position){
        return this.playgroundOwn.moveShip(id, position);
    }
    public boolean deleteShip(ShipID id){
        return this.playgroundOwn.deleteShip(id);
    }

    TurnResult.Error canShootAt(Position position) {
        return this.playgroundEnemy.canShootAt(position);
    }

    public TurnResult gotHit(Position position) {
        return this.playgroundOwn.gotHit(position);
    }

    public void update(ShotResult result){
        this.playgroundEnemy.updateField(result.getPosition(), result.getType());
    }

}
