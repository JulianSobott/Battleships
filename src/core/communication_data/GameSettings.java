package core.communication_data;

import core.Player;
import network.Connected;
import player.PlayerHuman;

public class GameSettings {

    private final int playgroundSize;
    private final Player p1;
    private final Player p2;
    private final Player startingPlayer;
    private final Connected networkConnection;
    private final boolean aiVsAi;
    private final long gameID;
    private final boolean slowAiShooting;
    private final boolean showHeatMap;


    public GameSettings(int playgroundSize, Player p1, Player p2, Connected networkConnection, Player startingPlayer,
                        boolean aiVsAi, boolean slowAiShooting, boolean showHeatMap, long gameID) {
        this.playgroundSize = playgroundSize;
        this.p1 = p1;
        this.p2 = p2;
        this.networkConnection = networkConnection;
        this.startingPlayer = startingPlayer;
        this.aiVsAi = aiVsAi;
        this.slowAiShooting = slowAiShooting;
        this.showHeatMap = showHeatMap;
        this.gameID = gameID;
    }

    public GameSettings(int playgroundSize, Player p1, Player p2) {
        this(playgroundSize, p1, p2, null, p1, false, false, true, 0);
    }

    public static GameSettings onlyInGame(boolean slowAiShooting, boolean showHeatMap) {
        return new GameSettings(0, null, null, null, null, false, slowAiShooting, showHeatMap, 0);
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

    public Player getStartingPlayer() {
        return startingPlayer;
    }

    public boolean isAiVsAi() {
        return aiVsAi;
    }

    public long getGameID() {
        return gameID;
    }

    public boolean isSlowAiShooting() {
        return slowAiShooting;
    }

    public boolean isShowHeatMap() {
        return showHeatMap;
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
