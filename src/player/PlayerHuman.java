package player;

import core.Player;
import core.communication_data.Position;

import java.beans.ConstructorProperties;

public class PlayerHuman extends Player {

    @ConstructorProperties({"index", "name", "playgroundSize"})
    public PlayerHuman(int index, String name, int playgroundSize) {
        super(index, name, playgroundSize);
    }

    @Override
    public boolean areAllShipsPlaced() {
        return this.playgroundOwn.areAllShipsPlaced();
    }

    @Override
    public Position makeTurn() {
        return null;
    }
}
