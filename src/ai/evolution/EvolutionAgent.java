package ai.evolution;

import ai.Agent;

public abstract class EvolutionAgent implements Agent {

    protected int epochSurvived = 0;
    protected double mutationFactor;
    protected int score = 0;
    private EvolutionNeuralNetwork network;
    private final int ID;
    private static int nextID = 0;

    public EvolutionAgent(int[] layers){
        this.ID = nextID++;
        this.network = new EvolutionNeuralNetwork(layers);
    }

    public void makeMove() {
        double[] input = this.getInputValues();
        double[] output = this.network.calculateOutput(input);
        this.handleOutputValues(output);
        this.updateScore();
    }

    protected abstract void updateScore();

    public void mutate(double mutationFactor){
        this.network.mutate(mutationFactor);
    }

    public abstract void initNewEpoch();

    public String getPerformanceData() {
        return String.format("Agent %d: epochSurvived=%d, score=%d", ID, epochSurvived, score);
    }
}
