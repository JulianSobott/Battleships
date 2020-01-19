package core.communication_data;

import java.io.Serializable;

public class ShipPosition extends Position implements Serializable {

    public ShipPosition() {

    }

    public ShipPosition(ShipPosition pos) {
        this(pos.x, pos.y, pos.direction, pos.length);
    }

    public enum Direction {
        HORIZONTAL, VERTICAL,
    }

    // TODO: lowercase names
    private Direction direction;
    private int length;

    public ShipPosition(int x, int y, Direction direction, int length) {
        super(x, y);
        assert length > 0: "Length must be greater than 0";
        this.direction = direction;
        this.length = length;
    }

    public static ShipPosition DEFAULT(int length){
        return new ShipPosition(0, 0, Direction.HORIZONTAL, length);
    }

    public Position[] generateIndices(){
        assert this.getLength() > 0;
        Position[] indices = new Position[this.getLength()];
        int x = 0;
        int y = 0;
        for (int i = 0; i < this.getLength(); i++) {
            indices[i] = new Position(this.x + x, this.y + y);
            if(this.getDirection() == ShipPosition.Direction.HORIZONTAL)
                x++;
            else
                y++;
        }
        return indices;
    }

    public Direction getDirection() {
        return direction;
    }

    public int getLength() {
        return length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ShipPosition that = (ShipPosition) o;
        return direction == that.direction && length == that.length && super.equals(o);
    }

    @Override
    public int hashCode() {
        int result = direction != null ? direction.hashCode() : 0;
        result = 31 * result + length;
        return result;
    }

    @Override
    public String toString() {
        return "ShipPosition{" +
                "DIRECTION=" + direction +
                ", LENGTH=" + length +
                ", X=" + x +
                ", Y=" + y +
                '}';
    }

    @Override
    public boolean isInRange(int minX, int minY, int maxX, int maxY) {
        if(super.isInRange(minX, minY, maxX, maxY)){
            if(this.direction == Direction.HORIZONTAL){
                // -1 because one part is already on position x, y
                return new Position(this.x + this.length - 1, this.y).isInRange(minX, minY, maxX, maxY);
            }else{
                // -1 because one part is already on position x, y
                return new Position(this.x, this.y + this.length - 1).isInRange(minX, minY, maxX, maxY);
            }
        }
        return false;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
