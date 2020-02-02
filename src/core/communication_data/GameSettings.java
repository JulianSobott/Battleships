package core.communication_data;

import core.Player;
import network.Connected;

public class GameSettings {

    private int playgroundSize;
    private Player p1;
    private Player p2;
    private Player startingPlayer;
    private Connected networkConnection;
    private boolean aiVsAi;
    private long gameID;
    private boolean slowAiShooting;
    private boolean showHeatMap;
    private boolean fromNetworkLoad;

    public GameSettings() {
    }

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

    public static GameSettings create() {
        return new GameSettings();
    }

    public GameSettings setPlaygroundSize(int playgroundSize) {
        this.playgroundSize = playgroundSize;
        return this;
    }

    public GameSettings setP1(Player p1) {
        this.p1 = p1;
        return this;
    }

    public GameSettings setP2(Player p2) {
        this.p2 = p2;
        return this;
    }

    public GameSettings setStartingPlayer(Player startingPlayer) {
        this.startingPlayer = startingPlayer;
        return this;
    }

    public GameSettings setNetworkConnection(Connected networkConnection) {
        this.networkConnection = networkConnection;
        return this;
    }

    public GameSettings setAiVsAi(boolean aiVsAi) {
        this.aiVsAi = aiVsAi;
        return this;
    }

    public GameSettings setGameID(long gameID) {
        this.gameID = gameID;
        return this;
    }

    public GameSettings setSlowAiShooting(boolean slowAiShooting) {
        this.slowAiShooting = slowAiShooting;
        return this;
    }

    public GameSettings setShowHeatMap(boolean showHeatMap) {
        this.showHeatMap = showHeatMap;
        return this;
    }

    @Override
    public String toString() {
        return "GameSettings{" +
                "playgroundSize=" + playgroundSize +
                ", p1=" + p1 +
                ", p2=" + p2 +
                ", startingPlayer=" + startingPlayer +
                ", networkConnection=" + networkConnection +
                ", aiVsAi=" + aiVsAi +
                ", gameID=" + gameID +
                ", slowAiShooting=" + slowAiShooting +
                ", showHeatMap=" + showHeatMap +
                '}';
    }

    public boolean isFromNetworkLoad() {
        return fromNetworkLoad;
    }

    public void setFromNetworkLoad(boolean fromNetworkLoad) {
        this.fromNetworkLoad = fromNetworkLoad;
    }
}
