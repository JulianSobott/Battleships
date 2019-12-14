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
    private List<Double> inValues = new ArrayList<>();
    private List<Double> outValues = new ArrayList<>();

    PassThroughAgent() {
        super(new int[]{1, 5, 1});
    }

    @Override
    protected void updateScore() {
        for (int i = 0; i < inValues.size(); i++) {
            double in = inValues.get(i);
            double out = outValues.get(i);
            double diff = Math.abs(out - in);
            this.score += (1 - diff) * 10;
        }
    }

    @Override
    public void initNewEpoch() {
        this.inValues.clear();
        this.outValues.clear();
        this.score = 0;
    }

    @Override
    public double[] getInputValues() {
        double value = random.nextDouble();
        inValues.add(value);
        return new double[]{value};
    }

    @Override
    public void handleOutputValues(double[] outputValues) {
        this.outValues.add(outputValues[1]);
    }
}
