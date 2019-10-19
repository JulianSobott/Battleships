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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ShipPosition that = (ShipPosition) o;
        return DIRECTION == that.DIRECTION && LENGTH == that.LENGTH && super.equals(o);
    }

    @Override
    public int hashCode() {
        int result = DIRECTION != null ? DIRECTION.hashCode() : 0;
        result = 31 * result + LENGTH;
        return result;
    }

    @Override
    public String toString() {
        return "ShipPosition{" +
                "DIRECTION=" + DIRECTION +
                ", LENGTH=" + LENGTH +
                ", X=" + X +
                ", Y=" + Y +
                '}';
    }
}
