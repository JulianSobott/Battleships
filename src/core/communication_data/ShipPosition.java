package core.communication_data;

import java.io.Serializable;

public class ShipPosition extends Position implements Serializable {

    public enum Direction {
        HORIZONTAL, VERTICAL,
    }

    // TODO: lowercase names
    private Direction DIRECTION;
    private int LENGTH;

    public ShipPosition(int x, int y, Direction DIRECTION, int LENGTH) {
        super(x, y);
        assert LENGTH > 0: "Length must be greater than 0";
        this.DIRECTION = DIRECTION;
        this.LENGTH = LENGTH;
    }

    public static ShipPosition DEFAULT(int length){
        return new ShipPosition(0, 0, Direction.HORIZONTAL, length);
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

    @Override
    public boolean isInRange(int minX, int minY, int maxX, int maxY) {
        if(super.isInRange(minX, minY, maxX, maxY)){
            if(this.DIRECTION == Direction.HORIZONTAL){
                // -1 because one part is already on position x, y
                return new Position(this.X + this.LENGTH - 1, this.Y).isInRange(minX, minY, maxX, maxY);
            }else{
                // -1 because one part is already on position x, y
                return new Position(this.X, this.Y + this.LENGTH - 1).isInRange(minX, minY, maxX, maxY);
            }
        }
        return false;
    }

    public void setDIRECTION(Direction DIRECTION) {
        this.DIRECTION = DIRECTION;
    }

    public void setLENGTH(int LENGTH) {
        this.LENGTH = LENGTH;
    }
}
