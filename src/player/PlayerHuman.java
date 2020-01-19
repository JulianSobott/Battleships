package player;

import core.playgrounds.PlaygroundEnemyBuildUp;
import core.playgrounds.PlaygroundOwnPlaceable;
import core.communication_data.Position;

import java.beans.ConstructorProperties;

public class PlayerHuman extends PlayerPlacer {

    @ConstructorProperties({"index", "name", "playgroundSize"})
    public PlayerHuman(int index, String name, int playgroundSize) {
        super(index, name, playgroundSize);
        this.playgroundOwn = new PlaygroundOwnPlaceable(playgroundSize);
        this.playgroundEnemy = new PlaygroundEnemyBuildUp(playgroundSize);
    }

    @Override
    public boolean areAllShipsPlaced() {
        return this.getPlaygroundOwn().areAllShipsPlaced();
    }

    @Override
    public Position makeTurn() {
        return null;
    }

    public PlaygroundOwnPlaceable getPlaygroundOwn() {
        return (PlaygroundOwnPlaceable) this.playgroundOwn;
    }
}
