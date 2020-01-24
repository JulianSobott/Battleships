package ai.evolution.sandbox;

import ai.evolution.EvolutionAgent;
import core.Playground;
import core.PlaygroundEnemy;
import core.PlaygroundOwn;
import core.communication_data.Position;
import core.communication_data.ShipList;
import core.communication_data.ShotResult;

public class BattleShipAgent extends EvolutionAgent {

    private PlaygroundOwn enemyPlayground;
    private PlaygroundEnemy myViewAtEnemyPlayground;
    private int playgroundSize = 5;
    private ShotResult res;
    private int round = 0;
    private final int numShips;
    private int numHits = 0;

    public BattleShipAgent(int[] layers) {
        super(layers);
        enemyPlayground = new PlaygroundOwn(playgroundSize);
        myViewAtEnemyPlayground = new PlaygroundEnemy(playgroundSize);
        enemyPlayground.placeShipsRandom();
        numShips = ShipList.fromSize(playgroundSize).getTotalNumberOfShips();
    }

    @Override
    protected void updateScore() {
        round++;
        if (res.getType() == Playground.FieldType.SHIP) {
            this.score += 10;
        }
        this.score -= round / 10;
    }

    @Override
    public void initNewEpoch() {

    }

    @Override
    public boolean isFinished() {
        return this.numHits == numShips;
    }

    @Override
    public double[] getInputValues() {
        double[] in = new double[playgroundSize * playgroundSize];
        for (int y = 0; y < playgroundSize; y++) {
            for (int x = 0; x < playgroundSize; x++) {
                in[y *playgroundSize + x] = fieldToValue(this.myViewAtEnemyPlayground.getElements()[y][x]);
            }
        }
        return in;
    }

    private double fieldToValue(Playground.Field f) {
        switch (f.type){
            case SHIP:
                if (f.hit) return 5;
                return 4;
            case WATER:
                if (f.hit) return 3;
                return 2;
        }
        assert false;
        return 0;
    }

    @Override
    public void handleOutputValues(double[] outputValues) {
        int x = (int) outputValues[0];
        int y = (int) outputValues[1];
        System.out.println("x=" + x +"y=" + y);
        if (!new Position(x, y).isOutsideOfPlayground(playgroundSize)) {
            res = this.enemyPlayground.gotHit(new Position(x, y));
            this.myViewAtEnemyPlayground.updateField(res.getPosition(), res.getType());
            if(res.getType() == Playground.FieldType.SHIP) {
                this.numHits++;
            }
        }else{
            this.score--;
        }

    }
}
