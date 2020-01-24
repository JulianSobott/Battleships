package ai.evolution.sandbox;

import ai.evolution.EvolutionAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Agent which goal is to output the value that were the input.
 */
public class PassThroughAgent extends EvolutionAgent {

    private static Random random = new Random(1000);
    private double inValue;
    private double outValue;

    PassThroughAgent() {
        super(new int[]{1, 5, 1});
    }

    @Override
    protected void updateScore() {
        double diff = Math.abs(outValue - inValue);
        this.score += (1 - diff) * 10;

    }

    @Override
    public void initNewEpoch() {
        this.score = 0;
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public double[] getInputValues() {
        inValue = random.nextDouble();
        return new double[]{inValue};
    }

    @Override
    public void handleOutputValues(double[] outputValues) {
        outValue = outputValues[0];
    }
}
