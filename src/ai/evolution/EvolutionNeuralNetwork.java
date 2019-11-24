package ai.evolution;


public class EvolutionNeuralNetwork {

    protected static final int IN_NEURONS_COUNT = 5 * 5;
    protected static final int HIDDEN_1_NEURONS_COUNT = 20;
    protected static final int OUT_NEURONS_COUNT = 2;

    protected EvolutionNeuron[] inNeurons = new EvolutionNeuron[IN_NEURONS_COUNT];
    protected EvolutionNeuron[] hidden1Neurons = new EvolutionNeuron[HIDDEN_1_NEURONS_COUNT];
    protected EvolutionNeuron[] outNeurons = new EvolutionNeuron[OUT_NEURONS_COUNT];

    public EvolutionNeuralNetwork(){
        for (int i = 0; i < EvolutionNeuralNetwork.IN_NEURONS_COUNT; i++) {
            this.inNeurons[i] = new EvolutionNeuron(HIDDEN_1_NEURONS_COUNT);
        }
        for (int i = 0; i < EvolutionNeuralNetwork.HIDDEN_1_NEURONS_COUNT; i++) {
            this.hidden1Neurons[i] = new EvolutionNeuron(OUT_NEURONS_COUNT);
        }
        for (int i = 0; i < EvolutionNeuralNetwork.OUT_NEURONS_COUNT; i++) {
            this.outNeurons[i] = new EvolutionNeuron(0);
        }
    }

    public double[] calculateOutput(double[] inputValues){
        assert inputValues.length == IN_NEURONS_COUNT: "Number of inputValues does not fit number of inputNeurons";
        for (int i = 0; i < EvolutionNeuralNetwork.IN_NEURONS_COUNT; i++) {
            this.inNeurons[i].setValue(inputValues[i]);
        }
        for (int i = 0; i < EvolutionNeuralNetwork.HIDDEN_1_NEURONS_COUNT; i++) {
            double sumValue = 0.0;
            for (int j = 0; j < EvolutionNeuralNetwork.IN_NEURONS_COUNT; j++) {
                sumValue += this.inNeurons[j].getWeightOutput(i);
            }
            double normalizedValue = EvolutionNeuron.activationFunction(sumValue);
            this.hidden1Neurons[i].setValue(normalizedValue);
        }

        double[] output = new double[OUT_NEURONS_COUNT];
        for (int i = 0; i < EvolutionNeuralNetwork.OUT_NEURONS_COUNT; i++) {
            double sumValue = 0.0;
            for (int j = 0; j < EvolutionNeuralNetwork.HIDDEN_1_NEURONS_COUNT; j++) {
                sumValue += this.hidden1Neurons[j].getWeightOutput(i);
            }
            double normalizedValue = EvolutionNeuron.activationFunction(sumValue);
            this.hidden1Neurons[i].setValue(normalizedValue);
            output[i] = normalizedValue;
        }
        return output;
    }


    public void mutate(double mutationFactor) {
        for (int i = 0; i < EvolutionNeuralNetwork.IN_NEURONS_COUNT; i++) {
            this.inNeurons[i].mutateWeights(mutationFactor);
        }
        for (int i = 0; i < EvolutionNeuralNetwork.HIDDEN_1_NEURONS_COUNT; i++) {
            this.hidden1Neurons[i].mutateWeights(mutationFactor);
        }
    }
}
