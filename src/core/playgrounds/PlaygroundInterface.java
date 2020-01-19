package core.playgrounds;

import core.Ship;

public interface PlaygroundInterface {

    int getSize();
    Playground.Field[][] getFields();
    void printField();
    Ship[] getAllShips();
}
