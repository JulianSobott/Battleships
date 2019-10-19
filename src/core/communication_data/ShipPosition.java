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

    public Position[] generateIndices(){
        assert this.getLENGTH() > 0;
        Position[] indices = new Position[this.getLENGTH()];
        int x = 0;
        int y = 0;
        for (int i = 0; i < this.getLENGTH(); i++) {
            indices[i] = new Position(this.X + x, this.Y + y);
            if(this.getDIRECTION() == ShipPosition.Direction.HORIZONTAL)
                x++;
            else
                y++;
        }
        return indices;
    }

    public Direction getDIRECTION() {
        return DIRECTION;
    }

    public int getLENGTH() {
        return LENGTH;
    }
}
