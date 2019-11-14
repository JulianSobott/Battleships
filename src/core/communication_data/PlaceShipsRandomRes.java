package core.communication_data;

import core.Playground;

public class PlaceShipsRandomRes {

    private final Playground.Field[][] elements;
    private final boolean successfully;

    private PlaceShipsRandomRes(Playground.Field[][] elements, boolean successfully) {
        this.elements = elements;
        this.successfully = successfully;
    }

    public PlaceShipsRandomRes(Playground.Field[][] elements) {
        this(elements, true);
    }

    public static PlaceShipsRandomRes failure() {
        return new PlaceShipsRandomRes(null, false);
    }

    public Playground.Field[][] getElements() {
        return elements;
    }

    public boolean isSuccessfully() {
        return successfully;
    }
}
