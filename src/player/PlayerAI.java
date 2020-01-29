package player;

import core.Player;
import core.Ship;
import core.communication_data.*;
import core.playgrounds.Playground;
import core.playgrounds.PlaygroundEnemyBuildUp;
import core.playgrounds.PlaygroundOwnPlaceable;
import core.utils.Random;
import core.utils.logging.LoggerLogic;
import core.utils.logging.LoggerProfile;
import java.beans.ConstructorProperties;
import java.util.ArrayList;

public class PlayerAI extends Player {

    public enum Difficulty {
        EASY, MEDIUM, HARD
    }

    private Difficulty difficulty;
    PlaygroundHeatmap playgroundHeatmap;

    ArrayList<Position> alreadyShot = new ArrayList<>();
    ArrayList<Position> potentialFields = new ArrayList<>();
    ArrayList<Position> shipfields = new ArrayList<>();

    int size;

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
        if (difficulty.equals(Difficulty.HARD)) {
            this.potentialFields = buildPotentialFields(playgroundSize);
        }
        this.size = playgroundSize;


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
            int x = Random.random.nextInt(getPlaygroundOwn().getSize());
            int y = Random.random.nextInt(getPlaygroundOwn().getSize());
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
     *     <li>in many iterations
     *          <ul>
     *              <li>place ships random (take already discovered fields in account.)</li>
     *              <li>Every field where a ship is placed increase a counter by one</li>
     *          </ul>
     *     </li>
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

    /**
     * Only Shoot at every second field, to improve speed of finding all ships.
     * <b>Algorithm:</b>
     * <ul>
     *     <li>just go trough the list of potenital fields randomly</li>
     *     <li>If a Ship got hit a single time go to shipSecondShot method with last element of shipfields as parameter</li>
     *     <li>else go to shipThirdShotPlus method with last element of shipfields as parameter</li>
     * </ul>
     *
     * @return Position
     */
    private Position makeMoveHard() {

        int randomNumber = Random.random.nextInt(potentialFields.size());
        Position target = potentialFields.get(randomNumber);
        if (shipfields.size() == 1) {

            target = shipSecondShot(shipfields.get(shipfields.size() - 1));
        } else if (shipfields.size() >= 2) {
            target = shipThirdShotPlus(shipfields.get(shipfields.size() - 1));  //probably endlos schleife...endlos schleife bei momentanem test nach zweitem versenkm Schiff?!
        }

        return target;

    }

    public Position shipSecondShot(Position lastHit) {
        Position[] possibleTargets = new Position[]{
                new Position(Math.min(size, lastHit.getX() + 1), lastHit.getY()),
                new Position(Math.max(0, lastHit.getX() - 1), lastHit.getY()),
                new Position(lastHit.getX(), Math.min(size, lastHit.getY() + 1)),
                new Position(lastHit.getX(), Math.max(0, lastHit.getY() - 1))
        };
        for (Position target : possibleTargets) {
            if (!alreadyShot.contains(target) && playgroundEnemy.canShootAt(target) == TurnResult.Error.NONE) {
                alreadyShot.add(target);
                potentialFields.remove(target);                                             // unnötig imho
                return target;
            }
        }
        assert false : "Should find at least one field";
        return null;
    }

    /**
     * Find a target that is adjacent to a ship and is possible to hit.
     *
     * @param lastHit A Position where the Ship was hit.
     * @return A valid Position that is adjacent to a ship
     */
    public Position shipThirdShotPlus(Position lastHit) {
        Ship lastShip = (Ship) this.playgroundEnemy.getFields()[lastHit.getY()][lastHit.getX()].element;
        assert lastShip.getStatus() == Ship.LifeStatus.ALIVE : "Should not shoot at already sunken ships: ship=" + lastShip;
        ShipPosition shipPosition = lastShip.getShipPosition();
        if (shipPosition.getDirection() == ShipPosition.Direction.HORIZONTAL) {
            // Try to shoot at field left of ship
            if (shipPosition.getX() - 1 >= 0) {
                Position target = new Position(shipPosition.getX() - 1, shipPosition.getY());
                if (playgroundEnemy.canShootAt(target) == TurnResult.Error.NONE) {
                    return target;
                } else {
                    // Can not shoot at the left side
                }
            }
            // Try to shoot at field right of the ship
            if (shipPosition.getX() + shipPosition.getLength() <= size) {
                Position target = new Position(shipPosition.getX() + shipPosition.getLength(), shipPosition.getY());
                if (playgroundEnemy.canShootAt(target) == TurnResult.Error.NONE) {
                    return target;
                } else {
                    // Can not shoot at the left side
                }
            }
        } else {    // Ship is Vertical
            // Try to shoot at field above of ship
            if (shipPosition.getY() - 1 >= 0) {
                Position target = new Position(shipPosition.getX(), shipPosition.getY() - 1);
                if (playgroundEnemy.canShootAt(target) == TurnResult.Error.NONE) {
                    return target;
                } else {
                    // Can not shoot at the top side
                }
            }
            // Try to shoot at field under of the ship
            if (shipPosition.getY() + shipPosition.getLength() <= size) {
                Position target = new Position(shipPosition.getX(), shipPosition.getY() + shipPosition.getLength());
                if (playgroundEnemy.canShootAt(target) == TurnResult.Error.NONE) {
                    return target;
                } else {
                    // Can not shoot at the bottom side
                }
            }
        }
        shipfields.remove(lastHit);
        Position nextLastHit = shipfields.get(shipfields.size() - 1);
        return shipThirdShotPlus(nextLastHit);
    }

    @Override
    public void update(ShotResult result) {

        super.update(result);
        alreadyShot.add(result.getPosition());
        this.playgroundHeatmap.update(result);

        if (result.getType().equals(Playground.FieldType.SHIP)) {
            shipfields.add(result.getPosition());
            ShotResultShip shotResultShip = (ShotResultShip) result;
            if (shotResultShip.getStatus() == Ship.LifeStatus.SUNKEN) {
                shipfields.clear();
            }
        }
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
