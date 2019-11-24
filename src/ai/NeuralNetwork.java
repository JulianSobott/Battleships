package ai;


public class NeuralNetwork {

    protected static final int IN_NEURONS_COUNT = 5 * 5;
    protected static final int HIDDEN_1_NEURONS_COUNT = 20;
    protected static final int OUT_NEURONS_COUNT = 2;

    protected Neuron[] inNeurons = new Neuron[IN_NEURONS_COUNT];
    protected Neuron[] hidden1Neurons = new Neuron[HIDDEN_1_NEURONS_COUNT];
    protected Neuron[] outNeurons = new Neuron[OUT_NEURONS_COUNT];

    public NeuralNetwork(){
        for (int i = 0; i < NeuralNetwork.IN_NEURONS_COUNT; i++) {
            this.inNeurons[i] = new Neuron(HIDDEN_1_NEURONS_COUNT);
        }
        for (int i = 0; i < NeuralNetwork.HIDDEN_1_NEURONS_COUNT; i++) {
            this.hidden1Neurons[i] = new Neuron(OUT_NEURONS_COUNT);
        }
        for (int i = 0; i < NeuralNetwork.OUT_NEURONS_COUNT; i++) {
            this.outNeurons[i] = new Neuron(0);
        }
    }

    public double[] calculateOutput(double[] inputValues){
        assert inputValues.length == IN_NEURONS_COUNT: "Number of inputValues does not fit number of inputNeurons";
        for (int i = 0; i < NeuralNetwork.IN_NEURONS_COUNT; i++) {
            this.inNeurons[i].setValue(inputValues[i]);
        }
        for (int i = 0; i < NeuralNetwork.HIDDEN_1_NEURONS_COUNT; i++) {
            double sumValue = 0.0;
            for (int j = 0; j < NeuralNetwork.IN_NEURONS_COUNT; j++) {
                sumValue += this.inNeurons[j].getWeightOutput(i);
            }
            double normalizedValue = Neuron.activationFunction(sumValue);
            this.hidden1Neurons[i].setValue(normalizedValue);
        }

        double[] output = new double[OUT_NEURONS_COUNT];
        for (int i = 0; i < NeuralNetwork.OUT_NEURONS_COUNT; i++) {
            double sumValue = 0.0;
            for (int j = 0; j < NeuralNetwork.HIDDEN_1_NEURONS_COUNT; j++) {
                sumValue += this.hidden1Neurons[j].getWeightOutput(i);
            }
            double normalizedValue = Neuron.activationFunction(sumValue);
            this.hidden1Neurons[i].setValue(normalizedValue);
            output[i] = normalizedValue;
        }
        return output;
    }


}
