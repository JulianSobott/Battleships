package core.communication_data;

public class Position {

    protected final int X, Y;

    public Position(int x, int y){
        this.X = x;
        this.Y = y;
    }

    public int getX() {
        return X;
    }

    public int getY() {
        return Y;
    }
}
