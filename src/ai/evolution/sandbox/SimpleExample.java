package ai.evolution.sandbox;

import ai.evolution.EvolutionSystem;

public class SimpleExample {

    public static void main(String[] args) {
        SimpleExample s = new SimpleExample();
        s.mainLoop();
    }

    private void mainLoop() {
        EvolutionSystem system = new SandboxEvolutionSystem(10);
        int maxEpochs = 90;
        for (int epoch = 0; epoch < maxEpochs; epoch++) {
            int maxRounds = 10;
            int rounds = 0;
            while(!system.update() && rounds < maxRounds){
                rounds++;
            }
            system.endEpoch();
        }
    }

}
