package ai.evolution.sandbox;

import ai.evolution.EvolutionSystem;

public class SandboxEvolutionSystem extends EvolutionSystem {

    SandboxEvolutionSystem(int numAgents) {
        super(numAgents);
    }

    @Override
    protected void createAgentsOnStart() {
        for (int i = 0; i < this.agents.length; i++) {
            this.agents[i] = new PassThroughAgent();
        }
    }
}
