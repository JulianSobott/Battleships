package ai.evolution;

public abstract class EvolutionSystem {

    protected EvolutionAgent[] agents;
    private int epochNum = 0;

    public EvolutionSystem(int numAgents) {
        this.agents = new EvolutionAgent[numAgents];
        this.createAgentsOnStart();
    }

    protected abstract void createAgentsOnStart();

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
        System.out.println(String.format("Epoch %d ended:", this.epochNum));
        EvolutionAgent[] agents = this.getSortedAgents();
        for (EvolutionAgent agent: agents) {
            System.out.println(agent.getPerformanceData());
        }
    }

    private void initAgents() {
        for(EvolutionAgent agent : agents)
            agent.initNewEpoch();
    }

    private void evolveAgents() {
        EvolutionAgent[] sortedAgents = getSortedAgents();
        // TODO: sort agents
        // TODO: pick agents
    }

    private EvolutionAgent[] getSortedAgents() {
        // TODO
        return this.agents;
    }
}
