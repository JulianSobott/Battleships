package player;

import core.Player;
import core.communication_data.Position;
import core.communication_data.ShotResult;
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
    PlaygroundHeatmap playgroundHeatmap;

    public PlayerAI(int index, String name, int playgroundSize) {
        super(index, name, playgroundSize);
        this.playgroundOwn.placeShipsRandom();
        this.playgroundOwn.printField();
        this.difficulty = Difficulty.MEDIUM;
        this.playgroundHeatmap = new PlaygroundHeatmap(playgroundSize);
    }

    @Override
    public boolean areAllShipsPlaced() {
        return true; // Ships are always placed in the constructor.
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

    /**
     * Search for a random where it is possible to shoot.
     * No logic nor intelligence.
     * @return Position
     */
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

    /**
     * Find the most possible fields, by trying many possible solutions.
     *
     * <b>Algorithm:</b>
     * <ul>
     *     <li>in many iterations</li>
     *          <ul>
     *              <li>place ships random (take already discovered fields in account.)</li>
     *              <li>Every field where a ship is placed increase a counter by one</li>
     *          </ul>
     *     <li>Return the field with the highest counter</li>
     * </ul>
     *
     * @return Position
     */
    private Position makeMoveMedium() {
        // The higher the better the prediction. Too high values can slow down the game
        int numPossiblePlacements = 1000;
        int[][] heatMap = this.playgroundHeatmap.buildHeatMap(numPossiblePlacements);
        int xMax = 0, yMax = 0, maxHeat = 0;
        PlaygroundHeatmap.printHeatMap(heatMap);
        for (int y = 0; y < this.playgroundEnemy.getSize(); y++) {
            for (int x = 0; x < this.playgroundEnemy.getSize(); x++) {
                if(heatMap[y][x] > maxHeat && !this.playgroundHeatmap.isAlreadyDiscoveredShipAt(x, y)) {
                    xMax = x;
                    yMax = y;
                    maxHeat = heatMap[y][x];
                }
            }
        }
        Position target = new Position(xMax, yMax);
        LoggerLogic.debug("Picked Position=" + target + " with heat=" + maxHeat);
        if (maxHeat == 0){
            LoggerLogic.error("Could not find a clever solution-> Making random move");
            return this.makeTurnEasy();
        }
        return target;
    }

    private Position makeMoveHard() {
        // TODO
        return makeTurnEasy();
    }



    @Override
    public void update(ShotResult result) {
        super.update(result);
        this.playgroundHeatmap.update(result);
    }
}
