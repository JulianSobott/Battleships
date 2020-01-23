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
import javafx.geometry.Pos;

import javax.swing.*;
import java.beans.ConstructorProperties;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class PlayerAI extends Player {

    public enum Difficulty {
        EASY, MEDIUM, HARD
    }

    private Difficulty difficulty;
    PlaygroundHeatmap playgroundHeatmap;

    Playground.FieldType lastresult;
    Position lastHitPosition;
    Position [] waterfields ;
    private ArrayList<Position> shipFields = new ArrayList<>();
    private ArrayList<Position> alreadyShot = new ArrayList<>();
    ArrayList<Position> potentialFields = new ArrayList<>();

    int randomValueBorder;
    Ship.LifeStatus lastShipSunken = null;

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
        round++;
        // The higher the better the prediction. Too high values can slow down the game
        int numPossiblePlacements = 1000;
        LoggerProfile.start("buildHeatMap_" + round);
        int[][] heatMap = this.playgroundHeatmap.buildHeatMap(numPossiblePlacements);
        LoggerProfile.stop("buildHeatMap_" + round);

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

        int playgroundSize = this.playgroundEnemy.getSize();
        Position target;

        do {
            if (!shipFields.isEmpty()/* || lastShipSunken == Ship.LifeStatus.SUNKEN*/){
                return fieldAround(lastHitPosition);
            }
            int randomGuess = Random.random.nextInt(potentialFields.size());
            LoggerLogic.debug("randomGuess: " + randomGuess);
            target = potentialFields.get(randomGuess);                // zufällige bestimmung eines der Felder welches markiert ist
            LoggerLogic.debug("Target Field: " + target);


        } while (!alreadyShot.contains(target) && (playgroundEnemy.canShootAt(target) != TurnResult.Error.NONE));
        alreadyShot.add(target);
    potentialFields.remove(target);
        return target;
    }

    public ArrayList<Position> checkPotential(ArrayList<Position> potentialFields){
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

    public Position fieldAround(Position p){
        int x = p.getX();
        int y = p.getY();
        int randomNumber;
        Position newTarget = p;

        while (newTarget == p || (alreadyShot.contains(newTarget) || (this.playgroundEnemy.canShootAt(newTarget) != TurnResult.Error.NONE))){
            randomNumber = Random.random.nextInt(4);
            LoggerLogic.debug("randomNumber: " + randomNumber);
            switch (randomNumber){
                case 0:
                    newTarget = new Position(x-1, y);
                    LoggerLogic.debug("new Target: " + newTarget);
                    break;
                case 1:
                    newTarget = new Position(x+1, y);
                    LoggerLogic.debug("new Target: " + newTarget);
                    break;
                case 2:
                    newTarget = new Position(x, y-1);
                    LoggerLogic.debug("new Target: " + newTarget);
                    break;
                default:                                                              //equivalent to case 3
                    newTarget = new Position(x, y+1);
                    LoggerLogic.debug("new Target: " + newTarget);
                    break;
            }
        }
//
        alreadyShot.add(newTarget);
        potentialFields.remove(newTarget);
        return newTarget;
    }

    @Override
    public void update(ShotResult result) {

        super.update(result);
        if (result.getType() == Playground.FieldType.SHIP) {
            shipFields.add(result.getPosition());
            lastresult = result.getType();
            lastHitPosition = result.getPosition();
            ShotResultShip resultShip = (ShotResultShip) result;

            potentialFields.remove(result.getPosition());

                if (resultShip.getStatus() == Ship.LifeStatus.SUNKEN) {
                    lastShipSunken = Ship.LifeStatus.SUNKEN;
                    // Berechne felder um shif herum, bekommt man das schon irgendwo?
                    waterfields = ((ShotResultShip) result).getWaterFields();
                    potentialFields = checkPotential(potentialFields);
                    shipFields.clear();
                }

        }
        this.playgroundHeatmap.update(result);
    }

    public ArrayList<Position> buildPotentialFields(int playgroundSize){
        for (int x = 0; x < playgroundSize; x++){
            for (int y = 0; y < playgroundSize; y++){
                if ((x % 2 == 0 && y % 2 == 1) || (x % 2 == 1 && y % 2 == 0)){
                    potentialFields.add(new Position(x, y));                 //jedes 2te feld is potentiel möglich, beginnend mit dem Feld an Stelle [0][1]
                    randomValueBorder++;                                                     //potentielle Felder werden in extra Array gespeichert, Array könnte/sollte man evtl auslagern?
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
        return (PlaygroundOwnPlaceable)playgroundOwn;
    }

    @Override
    public String toString() {
        return "{" + super.toString() + ", difficulty=" + difficulty + "}";
    }
}

//Fehler mit standardeinstellung, schiffe random platzieren lassen, nach versunkenem eigenem Schiff, während ich dran war
//    Exception in thread "JavaFX Application Thread" java.lang.NullPointerException
//        Exception in thread "JavaFX Application Thread" java.lang.NullPointerException
//        at javafx.graphics/com.sun.glass.ui.gtk.GtkApplication._runLoop(Native Method)
//        at javafx.graphics/com.sun.glass.ui.gtk.GtkApplication.lambda$runLoop$11(GtkApplication.java:277)
//        at java.base/java.lang.Thread.run(Thread.java:834)

//
//[Logic] [DEBUG] Player has already made a shot. player=PlayerHuman{name='1', index=0}
//        [2020-01-23 02:17:18] (core.GameManager.makeShot Line: 132, Thread: JavaFX Application Thread)
//        [Logic] [DEBUG] Make shot: player=PlayerHuman{name='1', index=0} position=Position{x=1, y=0}
//        [2020-01-23 02:17:18] (core.GameManager.makeShot Line: 123, Thread: JavaFX Application Thread)
//        [Logic] [DEBUG] {PlayerAI{name='2', index=1}=[], PlayerHuman{name='1', index=0}=[Position{x=1, y=2}]}
//        [2020-01-23 02:17:18] (core.GameManager.makeShot Line: 125, Thread: JavaFX Application Thread)
//        [Logic] [DEBUG] Position{x=1, y=0}
//        [2020-01-23 02:17:18] (core.GameManager.makeShot Line: 126, Thread: JavaFX Application Thread)
//        [Logic] [DEBUG] Player has already made a shot. player=PlayerHuman{name='1', index=0}
//        [2020-01-23 02:17:18] (core.GameManager.makeShot Line: 132, Thread: JavaFX Application Thread)
