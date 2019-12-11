package player;

import core.*;
import core.communication_data.Position;
import core.communication_data.ShipPosition;

public class PlaygroundTempAI extends PlaygroundOwn {

    public PlaygroundTempAI(int size) {
        super(size);
    }

    @Override
    public boolean canPlaceShip(ShipPosition position) {
        // Copied from PlaygroundOwn. Maybe find better solution! Only changed type==Ship to type==Water
        int[][] surroundingFields = {{-1, -1}, {-1, 0}, {0, -1}, {0, 0}, {0, 1}, {1, 0}, {1, 1}, {-1, 1}, {1, -1}};
        for (Position p : position.generateIndices()) {
            // position not on board
            if (position.isOutsideOfPlayground(this.size))
                return false;
            // surrounding field is ship
            for (int[] surroundingField : surroundingFields) {
                // 0 <= x < size
                int x = Math.min(Math.max(p.getX() + surroundingField[0], 0), this.size - 1);
                // 0 <= y < size
                int y = Math.min(Math.max(p.getY() + surroundingField[1], 0), this.size - 1);

                // Water or sunken ship
                if (this.elements[y][x] != null &&
                        (this.elements[y][x].type == FieldType.WATER ||
                                (this.elements[y][x].type == FieldType.SHIP &&
                                        ((Ship) this.elements[y][x].element).getStatus() == Ship.LifeStatus.SUNKEN)))
                    return false;
            }
        }
        return true;
    }

    public void copyPlayground(Playground playground) {
        assert playground.getSize() == this.getSize() : "Can not playground into other: Size is not the same: " +
                "this.size=" + this.getSize() + ", other.size=" + playground.getSize();
        Field[][] otherField = playground.getFields();
        for (int y = 0; y < playground.getSize(); y++) {
            for (int x = 0; x < playground.getSize(); x++) {
                if (otherField[y][x].type == FieldType.WATER) {
                    this.elements[y][x] = new Field(FieldType.WATER, new WaterElement(), true);
                } else if (otherField[y][x].type == FieldType.SHIP) {
                    // TODO: Handle sunken ships
                    this.elements[y][x] = new Field(FieldType.SHIP, null, true);
                } else {
                    this.elements[y][x] = new Field(FieldType.FOG, new FogElement(), true);
                }
            }
        }
    }

    public boolean isShipAt(Position position) {
        return this.elements[position.getY()][position.getX()].type == FieldType.SHIP;
    }
}
