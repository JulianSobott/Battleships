package core.communication_data;

import core.Player;
import network.Connected;

public class GameSettings {

    private final int playgroundSize;
    private final Player p1;
    private final Player p2;
    private final Connected networkConnection;

    public GameSettings(int playgroundSize, Player p1, Player p2, Connected networkConnection) {
        this.playgroundSize = playgroundSize;
        this.p1 = p1;
        this.p2 = p2;
        this.networkConnection = networkConnection;
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

    public Connected getNetworkConnection() {
        return networkConnection;
    }
}
