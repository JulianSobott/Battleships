package ai.evolution;

import ai.Agent;

public abstract class EvolutionAgent implements Agent {

    private int epochSurvived = 0;
    private double mutationFactor;
    private int score = 0;
    private EvolutionNeuralNetwork network;

    public EvolutionAgent(int[] layers){
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
}
