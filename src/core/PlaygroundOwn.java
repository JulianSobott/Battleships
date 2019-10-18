package core;

import core.communication_data.*;

public class PlaygroundOwn extends Playground {

    public PlaygroundOwn(int size) {
        super(size);
    }

    public PlaceShipResult placeShip(ShipPosition position){
        return null;
    }

    public PlaceShipResult moveShip(int id, ShipPosition position){
        return null;
    }

    public boolean deleteShip(int id){
        return false;
    }

    public boolean canPlaceShip(ShipPosition position){
        return false;
    }

    public void resetFieldsToWater(){

    }

    public void placeShipsRandom(ShipList shipList){

    }

    /**
     *
     * @param position x, y coordinates of the shot.
     * @return
     */
    public ShotResult gotHit(Position position){
        Field f = this.elements[position.getY()][position.getX()];
        f.hit = true;
        f.element.gotHit();
        if(f.type == FieldType.SHIP){
            return new ShotResultShip(position, FieldType.SHIP, ((Ship)f.element).getStatus());
        }else{
            return new ShotResultWater(position, f.type);
        }
    }
}
