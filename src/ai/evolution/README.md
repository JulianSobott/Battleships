# AI Algorithms

## Evolution

Based on the evolution in the nature. Survival of the fittest.
The goal is to train an agent that learns how to play the game and understand basic shooting.

For training multiple agents play against each other in parallel. For example 20 Agents play in 10 matches 1 vs 1. 
At the end of an epoch, the agents get ranked based on a score they reached. The score is based on multiple factors. 
Based on this scoreboard a new list of agents is created. It contains some agents from the last episode and some new. 
Agents can mutate. It is important to keep diversity and not only reach a local minimum. 
An epoch ends after a certain amount of turns.

### Score calculation

Considerations what can be taken in:

- Hits on ships
- Round needed to win
- Average hits per turn


### New generation mixture

Could experiment with following allocations
- 1/2 top half agents
- 1/4 random agents
- 1/4 new agents
