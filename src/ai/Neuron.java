package ai;

import java.util.Random;

public class Neuron {

    private double value;
    protected double[] out_weights;
    protected Random rand = new Random(5000);

    protected Neuron(int numOutWeights){
        this(0, new double[numOutWeights]);
    }

    Neuron(double value, double[] out_weights){
        this.value = value;
        this.out_weights = out_weights;
    }

    @Override
    protected Object clone() {
        return new Neuron(this.value, this.out_weights.clone());
    }

    void setWeightsRandom(){
        for (int i = 0; i < this.out_weights.length; i++) {
            double weightValue = this.rand.nextDouble();
            this.out_weights[i] = weightValue;
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
