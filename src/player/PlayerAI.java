package player;

import core.Player;
import core.Ship;
import core.communication_data.*;
import core.playgrounds.Playground;
import core.playgrounds.PlaygroundEnemyBuildUp;
import core.playgrounds.PlaygroundOwnPlaceable;
import core.utils.logging.LoggerLogic;
import core.utils.logging.LoggerProfile;
import javafx.geometry.Pos;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.Random;

public class PlayerAI extends Player {

    public enum Difficulty {
        EASY, MEDIUM, HARD
    }

    private Difficulty difficulty;
    PlaygroundHeatmap playgroundHeatmap;


    Position lastHitPosition;

    Position[] waterfields;
    private ArrayList<Position> fieldsAroundShiphit = new ArrayList<>();
    private ArrayList<Position> shipFields = new ArrayList<>();
    private ArrayList<Position> alreadyShot = new ArrayList<>();
    ArrayList<Position> potentialFields = new ArrayList<>();

    Position lastlastHitPosition;
    Ship.LifeStatus lastShipSunken = null;
    String direction = "";
    Position lastShotPostion;

    int round = 0;


    @ConstructorProperties({"index", "name", "playgroundSize", "difficulty"})
    public PlayerAI(int index, String name, int playgroundSize, Difficulty difficulty) {
        super(index, name, playgroundSize);
        this.playgroundOwn = new PlaygroundOwnPlaceable(playgroundSize);
        this.playgroundEnemy = new PlaygroundEnemyBuildUp(playgroundSize);
        getPlaygroundOwn().placeShipsRandom();
        getPlaygroundOwn().printField();
        this.difficulty = difficulty;
        this.playgroundHeatmap = new PlaygroundHeatmap(playgroundSize);
        this.potentialFields = buildPotentialFields(playgroundSize);


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
     *
     * @return Position
     */
    @Override
    public Position makeTurn() {
        Position pos = null;
        assert this.difficulty != null : "Difficulty is not set in PlayerAI";
        switch (this.difficulty) {
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
     *
     * @return Position
     */
    private Position makeTurnEasy() {
        Position pos;
        do {
            Random random = new Random();
            int x = random.nextInt(getPlaygroundOwn().getSize());
            int y = random.nextInt(getPlaygroundOwn().getSize());
            pos = new Position(x, y);
        } while (playgroundEnemy.canShootAt(pos) != TurnResult.Error.NONE);
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
        round++;
        // The higher the better the prediction. Too high values can slow down the game
        int numPossiblePlacements = 100;

        LoggerProfile.start("buildHeatMap_" + round);
        Position target = this.playgroundHeatmap.getHottestPosition(numPossiblePlacements);
        LoggerProfile.stop("buildHeatMap_" + round);

        if (target == null) {
            LoggerLogic.error("Could not find a clever solution-> Making random move");
            return this.makeTurnEasy();
        }

        return target;
    }

    private Position makeMoveHard() {

        Position target;
        if (!shipFields.isEmpty()) {
            return fieldsAround(lastHitPosition);
        }
        do {
            Random random = new Random();
            int randomGuess = random.nextInt(potentialFields.size());
            LoggerLogic.debug("randomGuess: " + randomGuess);
            target = potentialFields.get(randomGuess);                // zufällige bestimmung eines der Felder welches markiert ist
            LoggerLogic.debug("Target Field: " + target);


        } while (!alreadyShot.contains(target) && (playgroundEnemy.canShootAt(target) != TurnResult.Error.NONE));
        alreadyShot.add(target);
        potentialFields.remove(target);
        return target;
    }

    public ArrayList<Position> removeWaterFromPotential(ArrayList<Position> potentialFields) {
        if (waterfields != null) {
            for (int i = 0; i < waterfields.length; i++) {
                if (waterfields[i] != null) {
                    Position position = waterfields[i];
                    potentialFields.remove(position);
                }
            }
        }
        return potentialFields;
    }


    public Position fieldsAround(Position lastHitPosition) {
        if (lastHitPosition == lastlastHitPosition) {
            switch (direction) {
                case "right":
                    direction = "left";
                    break;
                case "left":
                    direction = "right";
                    break;
                case "up":
                    direction = "down";
                    break;
                case "down":
                    direction = "up";
                    break;
            }
        }
        direction = hitbeforelasthitDirection();
        int x = lastHitPosition.getX();
        int y = lastHitPosition.getY();
        Position target = new Position(-1, -1);
        if (!direction.equals("")) {
            switch (direction) {
                case "up":
                    target = new Position(x, y - 1);
                    break;
                case "down":
                    target = new Position(x, y + 1);
                    break;
                case "right":
                    target = new Position(x + 1, y);
                    break;
                case "left":
                    target = new Position(x - 1, y);
                    break;
            }

            alreadyShot.add(target);
            return target;
        }
        fieldsAroundShiphit.add(new Position(x + 1, y));
        fieldsAroundShiphit.add(new Position(x - 1, y));
        fieldsAroundShiphit.add(new Position(x, y + 1));
        fieldsAroundShiphit.add(new Position(x, y - 1));
        for (int i = 0; i < fieldsAroundShiphit.size(); i++) {
            if (alreadyShot.contains(fieldsAroundShiphit.get(i)) || !(playgroundEnemy.canShootAt(fieldsAroundShiphit.get(i)) == TurnResult.Error.NONE)) {
                fieldsAroundShiphit.remove(fieldsAroundShiphit.get(i));
            }
        }


        while (alreadyShot.contains(target) || !(playgroundEnemy.canShootAt(target) == TurnResult.Error.NONE)) {
            Random random = new Random();
            int switchnumber = random.nextInt(fieldsAroundShiphit.size());
            switch (switchnumber) {
                case 0:
                    target = new Position(fieldsAroundShiphit.get(0).getX(), fieldsAroundShiphit.get(0).getY());
                    break;
                case 1:
                    target = new Position(fieldsAroundShiphit.get(1).getX(), fieldsAroundShiphit.get(1).getY());
                    break;
                case 2:
                    target = new Position(fieldsAroundShiphit.get(2).getX(), fieldsAroundShiphit.get(2).getY());
                    break;
                case 3:
                    target = new Position(fieldsAroundShiphit.get(3).getX(), fieldsAroundShiphit.get(3).getY());
                    break;
            }

        }
        fieldsAroundShiphit.clear();
        alreadyShot.add(target);
        return target;
    }

    @Override
    public void update(ShotResult result) {
        lastlastHitPosition = lastHitPosition;
        lastShotPostion = result.getPosition();
        super.update(result);
        potentialFields.remove(result.getPosition());
        if (result.getType() == Playground.FieldType.SHIP) {
            shipFields.add(result.getPosition());

            lastHitPosition = result.getPosition();
            ShotResultShip resultShip = (ShotResultShip) result;
            if (resultShip.getStatus() == Ship.LifeStatus.SUNKEN) {
                lastHitPosition = null;
                lastlastHitPosition = null;
                shipFields.clear();
                lastShipSunken = Ship.LifeStatus.SUNKEN; // not sure if needed
                waterfields = ((ShotResultShip) result).getWaterFields();
                potentialFields = removeWaterFromPotential(potentialFields);
            }
        }


        this.playgroundHeatmap.update(result);
    }

    public String hitbeforelasthitDirection() {
        if ((lastHitPosition) == null || lastlastHitPosition == null) {
            return "";
        }
        int x = lastlastHitPosition.getX();
        int y = lastlastHitPosition.getY();
        int xlast = lastHitPosition.getX();
        int ylast = lastHitPosition.getY();


        if ((x < xlast)) {
            Position maybeTarget = new Position(xlast + 1, ylast);
            direction = "right";
            if (alreadyShot.contains(maybeTarget)) {
                maybeTarget = new Position(x - 1, ylast);
                direction = "left";
            }

        } else if ((x > xlast)) {
            Position maybeTarget = new Position(xlast - 1, ylast);
            direction = "left";
            if (alreadyShot.contains(maybeTarget)) {
                maybeTarget = new Position(x + 1, ylast);
                direction = "right";
            }
        } else if ((y < ylast)) {
            Position maybeTarget = new Position(xlast, ylast + 1);
            direction = "down";
            if (alreadyShot.contains(maybeTarget)) {
                maybeTarget = new Position(xlast, y - 1);
                direction = "up";
            }
        } else if ((y > ylast)) {
            Position maybeTarget = new Position(xlast, ylast - 1);
            direction = "up";
            if (alreadyShot.contains(maybeTarget)) {
                maybeTarget = new Position(xlast, y + 1);
                direction = "down";
            }
        }

        return direction;
    }

    public ArrayList<Position> buildPotentialFields(int playgroundSize) {
        for (int x = 0; x < playgroundSize; x++) {
            for (int y = 0; y < playgroundSize; y++) {
                if ((x % 2 == 0 && y % 2 == 1) || (x % 2 == 1 && y % 2 == 0)) {
                    potentialFields.add(new Position(x, y));                 //jedes 2te feld is potentiel möglich, beginnend mit dem Feld an Stelle [0][1]
                    //potentielle Felder werden in extra Array gespeichert, Array könnte/sollte man evtl auslagern?
                }
            }
        }
        return potentialFields;
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
        return (PlaygroundOwnPlaceable) playgroundOwn;
    }

    @Override
    public String toString() {
        return "{" + super.toString() + ", difficulty=" + difficulty + "}";
    }
}
