package ai;

public interface Agent {

    double[] getInputValues();
    void handleOutputValues(double[] outputValues);
    void makeMove();

}
