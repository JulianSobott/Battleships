package player;

import core.Player;
import core.communication_data.Position;
import core.communication_data.TurnResult;
import core.utils.logging.LoggerLogic;

import java.util.Random;

public class PlayerAI extends Player {

    enum Difficulty {
        EASY, MEDIUM, HARD
    }

    private Difficulty difficulty;
    private long seed = 1000;
    private Random r =  new Random(seed);

    public PlayerAI(String name, int playgroundSize) {
        super(name, playgroundSize);
        this.playgroundOwn.placeShipsRandom();
        this.playgroundOwn.printField();
        this.difficulty = Difficulty.EASY;
    }

    /**
     * Calculates a Position for the next shot. The Position is a completely valid position.
     * @return Position
     */
    @Override
    public Position makeTurn() {
        Position pos = null;
        assert this.difficulty != null: "Difficulty is not set in PlayerAI";
        switch (this.difficulty){
            case EASY:
                pos = this.makeTurnEasy();
                break;
            case MEDIUM:
                pos = this.makeMoveMedium();
                break;
            case HARD:
                pos = this.makeMoveHard();
                break;
            default:
                LoggerLogic.error("Difficulty is not set correctly in PlayerAI: difficulty=" + this.difficulty);
                pos = this.makeMoveMedium();
        }
        return pos;
    }

    private Position makeTurnEasy() {
        Position pos;
        do {
            int x = r.nextInt(this.playgroundOwn.getSize());
            int y = r.nextInt(this.playgroundOwn.getSize());
            pos = new Position(x, y);
        }while (playgroundEnemy.canShootAt(pos) != TurnResult.Error.NONE);
        LoggerLogic.info("PlayerAI.makeTurn: position=" + pos);
        return pos;
    }

    private Position makeMoveMedium() {
        // TODO
        return makeTurnEasy();
    }

    private Position makeMoveHard() {
        // TODO
        return makeTurnEasy();
    }
}
