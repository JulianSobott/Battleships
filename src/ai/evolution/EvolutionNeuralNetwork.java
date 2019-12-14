package ai.evolution;


public class EvolutionNeuralNetwork {

    protected final int IN_NEURONS_COUNT;
    protected final int HIDDEN_1_NEURONS_COUNT;
    protected final int OUT_NEURONS_COUNT;

    protected EvolutionNeuron[] inNeurons;
    protected EvolutionNeuron[] hidden1Neurons;
    protected EvolutionNeuron[] outNeurons;

    public EvolutionNeuralNetwork(int[] layers){
        assert layers.length == 3: "For now only Networks with exactly 3 layers are possible";
        this.IN_NEURONS_COUNT = layers[0];
        this.HIDDEN_1_NEURONS_COUNT = layers[1];
        this.OUT_NEURONS_COUNT = layers[2];
        this.inNeurons = new EvolutionNeuron[IN_NEURONS_COUNT];
        this.hidden1Neurons = new EvolutionNeuron[HIDDEN_1_NEURONS_COUNT];
        this.outNeurons = new EvolutionNeuron[OUT_NEURONS_COUNT];
        for (int i = 0; i < IN_NEURONS_COUNT; i++) {
            this.inNeurons[i] = new EvolutionNeuron(HIDDEN_1_NEURONS_COUNT);
        }
        for (int i = 0; i < HIDDEN_1_NEURONS_COUNT; i++) {
            this.hidden1Neurons[i] = new EvolutionNeuron(OUT_NEURONS_COUNT);
        }
        for (int i = 0; i < OUT_NEURONS_COUNT; i++) {
            this.outNeurons[i] = new EvolutionNeuron(0);
        }
    }

    public double[] calculateOutput(double[] inputValues){
        assert inputValues.length == IN_NEURONS_COUNT: "Number of inputValues does not fit number of inputNeurons";
        for (int i = 0; i < IN_NEURONS_COUNT; i++) {
            this.inNeurons[i].setValue(inputValues[i]);
        }
        for (int i = 0; i < HIDDEN_1_NEURONS_COUNT; i++) {
            double sumValue = 0.0;
            for (int j = 0; j < IN_NEURONS_COUNT; j++) {
                sumValue += this.inNeurons[j].getWeightOutput(i);
            }
            double normalizedValue = EvolutionNeuron.activationFunction(sumValue);
            this.hidden1Neurons[i].setValue(normalizedValue);
        }

        double[] output = new double[OUT_NEURONS_COUNT];
        for (int i = 0; i < OUT_NEURONS_COUNT; i++) {
            double sumValue = 0.0;
            for (int j = 0; j < HIDDEN_1_NEURONS_COUNT; j++) {
                sumValue += this.hidden1Neurons[j].getWeightOutput(i);
            }
            double normalizedValue = EvolutionNeuron.activationFunction(sumValue);
            this.hidden1Neurons[i].setValue(normalizedValue);
            output[i] = normalizedValue;
        }
        return output;
    }


    public void mutate(double mutationFactor) {
        for (int i = 0; i < IN_NEURONS_COUNT; i++) {
            this.inNeurons[i].mutateWeights(mutationFactor);
        }
        for (int i = 0; i < HIDDEN_1_NEURONS_COUNT; i++) {
            this.hidden1Neurons[i].mutateWeights(mutationFactor);
        }
    }
}
