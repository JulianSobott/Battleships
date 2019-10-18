package core.communication_data;

public class ShipPosition extends Position{

    public enum Direction {
        HORIZONTAL, VERTICAL,
    }

    private final Direction DIRECTION;
    private final int LENGTH;

    public ShipPosition(int x, int y, Direction DIRECTION, int LENGTH) {
        super(x, y);
        this.DIRECTION = DIRECTION;
        this.LENGTH = LENGTH;
    }

    public Direction getDIRECTION() {
        return DIRECTION;
    }

    public int getLENGTH() {
        return LENGTH;
    }
}
