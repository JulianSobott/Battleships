package ai.evolution.sandbox;

import ai.evolution.EvolutionSystem;

public class SimpleExample {

    public static void main(String[] args) {
        SimpleExample s = new SimpleExample();
        s.mainLoop();
    }

    private void mainLoop() {
        EvolutionSystem system = new SandboxEvolutionSystem(10);
        int maxEpochs = 10000;
        for (int epoch = 0; epoch < maxEpochs; epoch++) {
            int numTurns = 20;
            for (int i = 0; i < numTurns; i++) {
                system.update();
            }
            system.endEpoch();
        }
    }

}
