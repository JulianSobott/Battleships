package core.communication_data;

import java.io.Serializable;

public class Position implements Serializable {

    protected final int X, Y;

    public Position(int x, int y){
        assert x >= 0 && y >= 0: "No negative positions are allowed";
        this.X = x;
        this.Y = y;
    }

    public int getX() {
        return X;
    }

    public int getY() {
        return Y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Position position = (Position) o;

        if (X != position.X) return false;
        return Y == position.Y;
    }

    @Override
    public int hashCode() {
        int result = X;
        result = 31 * result + Y;
        return result;
    }

    @Override
    public String toString() {
        return "Position{" +
                "X=" + X +
                ", Y=" + Y +
                '}';
    }

    /**Is the position inside the min and max values
     *
     * @param minX min X value inclusive
     * @param minY min Y value inclusive
     * @param maxX max X value inclusive
     * @param maxY max Y value inclusive
     * @return true if position is in range, false otherwise
     */
    public boolean isInRange(int minX, int minY, int maxX, int maxY){
        return this.X >= minX && this.X <= maxX && this.Y >= minY && this.Y <= maxY;
    }

    public boolean isOutsideOfPlayground(int size){
        // -1 because indexing starts at 0
        return !this.isInRange(0, 0, size - 1, size - 1);
    }
}
