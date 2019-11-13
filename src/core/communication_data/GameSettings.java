package core.communication_data;

import core.Player;

public class GameSettings {

    private final int playgroundSize;
    private final Player p1;
    private final Player p2;

    public GameSettings(int playgroundSize, Player p1, Player p2) {
        this.playgroundSize = playgroundSize;
        this.p1 = p1;
        this.p2 = p2;
    }

    public int getPlaygroundSize() {
        return playgroundSize;
    }

    public Player getP1() {
        return p1;
    }

    public Player getP2() {
        return p2;
    }
}
