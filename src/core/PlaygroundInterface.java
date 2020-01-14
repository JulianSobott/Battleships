package core;

public interface PlaygroundInterface {

    int getSize();
    Playground.Field[][] getFields();
    void printField();
    Ship[] getAllShips();
}
