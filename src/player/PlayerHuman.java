package player;

import core.playgrounds.PlaygroundEnemyBuildUp;
import core.playgrounds.PlaygroundOwnPlaceable;
import core.communication_data.Position;
import core.utils.logging.LoggerLogic;

import java.beans.ConstructorProperties;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PlayerHuman extends PlayerPlacer {

    private final ConcurrentLinkedQueue<Position> nextShots = new ConcurrentLinkedQueue<>();

    @ConstructorProperties({"index", "name", "playgroundSize"})
    public PlayerHuman(int index, String name, int playgroundSize) {
        super(index, name, playgroundSize);
        this.playgroundOwn = new PlaygroundOwnPlaceable(playgroundSize);
        this.playgroundEnemy = new PlaygroundEnemyBuildUp(playgroundSize);
    }

    public void addNextShot(Position position) {
        synchronized (this.nextShots) {
            if (this.nextShots.size() != 1) {
                this.nextShots.add(position);
                this.nextShots.notifyAll();
            } else {
                LoggerLogic.info("Player has already made a shot. Shot is ignored. player=" + this);
            }
        }
    }

    @Override
    public boolean areAllShipsPlaced() {
        return this.getPlaygroundOwn().areAllShipsPlaced();
    }

    @Override
    public Position makeTurn() {
        synchronized (this.nextShots) {
            while (this.nextShots.isEmpty()) {
                try {
                    LoggerLogic.debug("Queue "+ this.nextShots +" was empty: waiting...");
                    this.nextShots.wait();
                    LoggerLogic.debug("Queue is no longer empty");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }
            return this.nextShots.poll();
        }
    }

    public PlaygroundOwnPlaceable getPlaygroundOwn() {
        return (PlaygroundOwnPlaceable) this.playgroundOwn;
    }
}
