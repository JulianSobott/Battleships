package core;

import core.communication_data.*;
import core.utils.Logger;

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
        Logger.debug("Place: ", position);
        PlaceShipResult res = this.playgroundOwn.placeShip(position);
        this.playgroundOwn.printField();
        Logger.debug(res);
        return res;
    }
    public PlaceShipResult moveShip(ShipID id, ShipPosition position){
        Logger.debug("Move: ", id, position);
        PlaceShipResult res = this.playgroundOwn.moveShip(id, position);
        this.playgroundOwn.printField();
        Logger.debug(res);
        return res;
    }
    public boolean deleteShip(ShipID id){
        Logger.debug("Delete: ", id);
        boolean res = this.playgroundOwn.deleteShip(id);
        this.playgroundOwn.printField();
        Logger.debug(res);
        return res;
    }

    public ShotResult gotHit(Position position){
        return this.playgroundOwn.gotHit(position);
    }

    public void update(ShotResult result){
        this.playgroundEnemy.updateField(result.getPosition(), result.getType());
    }

    public PlaceShipsRandomRes placeShipsRandom() {
        return this.playgroundOwn.placeShipsRandom();
    }

    public boolean allEnemyShipsSunken(){
        return this.playgroundEnemy.areAllShipsSunken();
    }

    TurnResult.Error canShootAt(Position position) {
        return this.playgroundEnemy.canShootAt(position);
    }
}
