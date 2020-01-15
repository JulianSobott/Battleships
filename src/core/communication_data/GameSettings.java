package core.communication_data;

import core.Player;
import network.Connected;
import player.PlayerHuman;

public class GameSettings {

    private final int playgroundSize;
    private final PlayerHuman p1;
    private final Player p2;
    private final Player startingPlayer;
    private final Connected networkConnection;

    public GameSettings(int playgroundSize, PlayerHuman p1, Player p2, Connected networkConnection, Player startingPlayer) {
        this.playgroundSize = playgroundSize;
        this.p1 = p1;
        this.p2 = p2;
        this.networkConnection = networkConnection;
        this.startingPlayer = startingPlayer;
    }

    public GameSettings(int playgroundSize, PlayerHuman p1, Player p2) {
        this(playgroundSize, p1, p2, null, p1);
    }

    public int getPlaygroundSize() {
        return playgroundSize;
    }

    public PlayerHuman getP1() {
        return p1;
    }

    public Player getP2() {
        return p2;
    }

    public Connected getNetworkConnection() {
        return networkConnection;
    }

    public Player getStartingPlayer() {
        return startingPlayer;
    }

    @Override
    public String toString() {
        return "GameSettings{" +
                "playgroundSize=" + playgroundSize +
                ", p1=" + p1 +
                ", p2=" + p2 +
                ", startingPlayer=" + startingPlayer +
                ", networkConnection=" + networkConnection +
                '}';
    }
}
