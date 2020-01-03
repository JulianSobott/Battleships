package core.communication_data;

import java.io.Serializable;

public class Position implements Serializable {

    protected int x, y;

    // x = col, y = row
    public Position(int x, int y){
        assert x >= 0 && y >= 0: "No negative positions are allowed";
        this.x = x;
        this.y = y;
    }

    /**
     * @param pos Positions in form: {{x, y}, {x, y}, ...}
     * @return Position[]
     */
    public static Position[] intArrayToPositionArray(int[][] pos) {
        Position[] positions = new Position[pos.length];
        int i = 0;
        for (int[] p : pos) {
            positions[i] = new Position(p[0], p[1]);
        }
        return positions;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Position position = (Position) o;

        if (x != position.x) return false;
        return y == position.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    @Override
    public String toString() {
        return "Position{" +
                "x=" + x +
                ", y=" + y +
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
        return this.x >= minX && this.x <= maxX && this.y >= minY && this.y <= maxY;
    }

    public boolean isOutsideOfPlayground(int size){
        // -1 because indexing starts at 0
        return !this.isInRange(0, 0, size - 1, size - 1);
    }
}
