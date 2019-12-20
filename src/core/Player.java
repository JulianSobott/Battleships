package core;

import core.communication_data.*;

public abstract class Player {

    protected PlaygroundOwn playgroundOwn;
    protected PlaygroundEnemy playgroundEnemy;

    protected String name;

    public Player(String name, int playgroundSize) {
        this.name = name;
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

    public PlaceShipsRandomRes placeShipsRandom() {
        return this.playgroundOwn.placeShipsRandom();
    }

    public boolean allEnemyShipsSunken() {
        return this.playgroundEnemy.areAllShipsSunken();
    }

    TurnResult.Error canShootAt(Position position) {
        return this.playgroundEnemy.canShootAt(position);
    }
}
