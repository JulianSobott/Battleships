package ai.evolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public abstract class EvolutionSystem {

    protected EvolutionAgent[] agents;
    protected final int NUM_AGENTS;
    private int epochNum = 0;

    public EvolutionSystem(int numAgents) {
        this.NUM_AGENTS = numAgents;
        this.agents = new EvolutionAgent[numAgents];
        this.agents = this.getNewAgents(NUM_AGENTS);
    }

    protected abstract EvolutionAgent[] getNewAgents(int numAgents);

    public void update() {
        for(EvolutionAgent agent : this.agents){
            agent.makeMove();
        }
    }

    public void endEpoch() {
        this.outputPerformanceData();
        this.evolveAgents();
        this.initAgents();
        this.epochNum++;
    }

    private void outputPerformanceData() {
        if (this.epochNum % 100 == 0) {
            System.out.println(String.format("Epoch %d ended:", this.epochNum));
            EvolutionAgent[] agents = this.getSortedAgents();
            System.out.println(String.format("Best score was: %d", agents[0].score));
            for (EvolutionAgent agent: agents) {
                System.out.println(agent.getPerformanceData());
            }
        }
    }

    private void initAgents() {
        for(EvolutionAgent agent : agents)
            agent.initNewEpoch();
    }

    private void evolveAgents() {
        EvolutionAgent[] sortedAgents = getSortedAgents();
        int numKeep = NUM_AGENTS / 2;
        int numReplace = NUM_AGENTS - numKeep;
        for (int i = 0; i < numKeep; i++) {
            this.agents[i] = sortedAgents[i];
            this.agents[i].epochSurvived++;
            this.agents[i].mutate();
        }
        EvolutionAgent[] newAgents = this.getNewAgents(numReplace);
        System.arraycopy(newAgents, 0, this.agents, numKeep, numReplace);
    }

    private EvolutionAgent[] getSortedAgents() {
        List<EvolutionAgent> agentsList = Arrays.asList(this.agents);
        agentsList.sort(Comparator.reverseOrder());
        return agentsList.toArray(new EvolutionAgent[0]);
    }
}
