package player;

import core.Player;
import core.playgrounds.PlaygroundEnemyBuildUp;
import core.playgrounds.PlaygroundOwnPlaceable;
import core.communication_data.Position;
import core.communication_data.ShotResult;
import core.communication_data.TurnResult;
import core.utils.Random;
import core.utils.logging.LoggerLogic;

import java.beans.ConstructorProperties;

public class PlayerAI extends Player {

    public enum Difficulty {
        EASY, MEDIUM, HARD
    }

    private Difficulty difficulty;
    PlaygroundHeatmap playgroundHeatmap;

    @ConstructorProperties({"index", "name", "playgroundSize", "difficulty"})
    public PlayerAI(int index, String name, int playgroundSize, Difficulty difficulty) {
        super(index, name, playgroundSize);
        this.playgroundOwn = new PlaygroundOwnPlaceable(playgroundSize);
        this.playgroundEnemy = new PlaygroundEnemyBuildUp(playgroundSize);
        getPlaygroundOwn().placeShipsRandom();
        getPlaygroundOwn().printField();
        this.difficulty = difficulty;
        this.playgroundHeatmap = new PlaygroundHeatmap(playgroundSize);
    }

    public PlayerAI(int index, String name, int playgroundSize) {
        this(index, name, playgroundSize, Difficulty.MEDIUM);
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
            int x = Random.random.nextInt(getPlaygroundOwn().getSize());
            int y = Random.random.nextInt(getPlaygroundOwn().getSize());
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
        // TODO evtl auslagern der 'PotentialFields', merken ob getroffen wurde um in der Nähe weiter zu schiessen(gibts dafür evtl schon ne Methode?)
        int k = this.playgroundEnemy.getSize();
        int iterator = 0;
        Position target;
        Position tuple;
        Position [] potentialFields = new Position[(k * k) / 2];
        for (int i = 0; i < k; i++){
            for (int j = 0; j < k; j++){
                if ((i % 2 == 0 && j % 2 == 1) || (i % 2 == 1 && j % 2 == 0)){
                    potentialFields[iterator] = new Position(i, j);                 //jedes 2te feld is potentiel möglich, beginnend mit dem Feld an Stelle [0][1]
                    iterator++;                                                     //potentielle Felder werden in extra Array gespeichert, Array könnte/sollte man evtl auslagern?
                }
            }
        }
        int randomGuess =  (int)(Math.random() * iterator);   // Ist hier nicht ne zahl > arraylength moeglich? evtl ArrayoutofBounds?
        target = potentialFields[randomGuess];                // zufällige bestimmung eines der Felder welches markiert ist
        System.out.println(target);
        System.out.println(randomGuess);
        return target;
    }



    @Override
    public void update(ShotResult result) {
        super.update(result);
        this.playgroundHeatmap.update(result);
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public PlaygroundHeatmap getPlaygroundHeatmap() {
        return playgroundHeatmap;
    }

    public void setPlaygroundHeatmap(PlaygroundHeatmap playgroundHeatmap) {
        this.playgroundHeatmap = playgroundHeatmap;
    }

    public void setPlaygroundEnemy(PlaygroundEnemyBuildUp playgroundEnemy) {

        super.setPlaygroundEnemy(playgroundEnemy);
        //this.playgroundHeatmap.updateByPlayground();
    }

    public PlaygroundOwnPlaceable getPlaygroundOwn() {
        return (PlaygroundOwnPlaceable)playgroundOwn;
    }


}
