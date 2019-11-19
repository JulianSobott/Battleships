package core;

import core.communication_data.TurnResult;

public abstract class GameController {

    abstract void placeShips();

    abstract void startShooting();

    abstract TurnResult makeOwnTurn();

    abstract TurnResult makeEnemyTurn();

    void gameLoop() {
        boolean gameFinished = false;
        while (!gameFinished) {
            TurnResult res = makeOwnTurn();
            if (res.isFINISHED()) gameFinished = true;
            // TODO
        }

    }


}
