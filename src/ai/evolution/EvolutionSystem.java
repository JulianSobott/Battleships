package ai.evolution;

import ai.Agent;

public class EvolutionSystem {

    EvolutionAgent[] agents;

    public EvolutionSystem(int numAgents) {
        this.agents = new EvolutionAgent[numAgents];
    }

    public void update() {
        for(EvolutionAgent agent : this.agents){
            agent.makeMove();
        }
    }

    public void endEpoch() {
        this.evolveAgents();
        this.initAgents();
    }

    private void initAgents() {
        for(EvolutionAgent agent : agents)
            agent.initNewEpoch();
    }

    private void evolveAgents() {
        EvolutionAgent[] sortedAgents = new EvolutionAgent[this.agents.length];
        // TODO: sort agents
        // TODO: pick agents
    }
}
