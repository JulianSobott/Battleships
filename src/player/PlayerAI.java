package player;

import core.Player;
import core.Playground;
import core.PlaygroundEnemy;
import core.PlaygroundOwn;
import core.communication_data.Position;
import core.communication_data.ShipList;
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
        this.difficulty = Difficulty.MEDIUM;
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
        int size = this.playgroundEnemy.getSize();
        int[][] fieldCounters = new int[size][size];
        PlaygroundTempAI tempPlayground = new PlaygroundTempAI(size);
        tempPlayground.copyPlayground(this.playgroundEnemy);    // TODO: Find alternative to coping it every time
        int numIterations = 1;
        for(int iteration = 0; iteration < numIterations; iteration++){
            // TODO: ensure that ships are placed on exiting hits. write complete new method
            //tempPlayground.placeShipsRandom();
            for(int y = 0; y < size; y++){
                for(int x = 0; x < size; x++) {
                    fieldCounters[y][x] += tempPlayground.isShipAt(new Position(x, y)) ? 1 : 0;
                }
            }
        }
        this.printHeatMap(fieldCounters);
        return makeTurnEasy();
    }

    private Position makeMoveHard() {
        // TODO
        return makeTurnEasy();
    }

    private void printHeatMap(int[][] map){
        StringBuilder s = new StringBuilder();
        s.append("\n");
        for(int[] row : map){
            for(int heat : row){
                s.append(heat);
            }
            s.append("\n");
        }
        LoggerLogic.debug(s.toString());
    }
}
