package ai.evolution;

import ai.Neuron;

import java.util.Random;

public class EvolutionNeuron {

    private double value;
    protected double[] out_weights;
    protected Random rand = new Random(5000);

    protected EvolutionNeuron(int numOutWeights){
        this(0, new double[numOutWeights]);
    }

    EvolutionNeuron(double value, double[] out_weights){
        this.value = value;
        this.out_weights = out_weights;
    }

    @Override
    protected Object clone() {
        return new EvolutionNeuron(this.value, this.out_weights.clone());
    }

    void setWeightsRandom(){
        for (int i = 0; i < this.out_weights.length; i++) {
            double weightValue = this.rand.nextDouble();
            this.out_weights[i] = weightValue;
        }
    }

    void mutateWeights(double mutationFactor) {
        assert mutationFactor > 0. : "mutationFactor must be positive";
        for (int i = 0; i < this.out_weights.length; i++) {
            // Improvements could be made in this calculation
            double newWeightValue = this.out_weights[i] + (this.rand.nextDouble() * mutationFactor) - 0.5;
            this.out_weights[i] = newWeightValue;
        }
    }

    double getWeightOutput(int i){
        assert i > 0 && i < this.out_weights.length: "Weight with index " + i + "does not exist";
        return this.value * this.out_weights[i];
    }

    static double activationFunction(double sumValue) {
        return Math.min(1., Math.max(0., sumValue));
    }

    double getValue() {
        return value;
    }

    void setValue(double value) {
        this.value = value;
    }

    static double setBetween(double value){
        return Math.max(0., Math.min(1., value));
    }
}
