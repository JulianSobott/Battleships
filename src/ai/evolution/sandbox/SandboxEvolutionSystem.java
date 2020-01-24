package ai.evolution.sandbox;

import ai.evolution.EvolutionAgent;
import ai.evolution.EvolutionSystem;

public class SandboxEvolutionSystem extends EvolutionSystem {

    SandboxEvolutionSystem(int numAgents) {
        super(numAgents);
    }

    @Override
    protected EvolutionAgent[] getNewAgents(int numAgents) {
        EvolutionAgent[] newAgents = new EvolutionAgent[numAgents];
        for (int i = 0; i < numAgents; i++) {
            newAgents[i] = new BattleShipAgent(new int[]{5, 10, 2});
        }
        return newAgents;
    }
}
