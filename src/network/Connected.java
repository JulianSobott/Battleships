package network;

import core.Ship;
import core.communication_data.Position;
import core.communication_data.ShotResult;
import core.communication_data.ShotResultShip;
import core.playgrounds.Playground;
import core.utils.logging.LoggerNetwork;
import gui.PlayGame.InGameGUI;
import player.PlayerNetwork;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public abstract class Connected {

    private static final int SHOOT_INDEX_X = 1;
    private static final int SHOOT_INDEX_Y = 0;
    // First possible position where can be shot
    private static final int SHOOT_OFFSET = 1;


    // Network
    protected BufferedReader in = null;
    protected BufferedWriter out = null; //setzen damit immerhin null drinsteht bei voreiliger ansprache
    private Socket socket;

    // Communication
    protected boolean isRunning = false;
    protected boolean isStarted = false;
    protected boolean isStartingPlayer = false;
    protected final AtomicReference<String> expectedMessage = new AtomicReference<>();
    protected String expectedFirstMessage;
    protected PlayerNetwork player;

    // Data
    private final HashMap<String, Object> sentData = new HashMap<>();
    private InGameGUI inGameGUI;


    public abstract ConnectionStatus start();

    /**
     * Start the communication in a new thread.
     * All mesages are processed till one side closes the connection or the game is over.
     */
    public void startCommunication() {
        Thread t = new Thread(this::waitMessage);
        t.setName("Network_waitMessage");
        t.start();
    }

    public void connected(Socket socket) {
        this.socket = socket;
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendAllShipsPlaced() {
        sendMessage("confirmed");
    }

    public void enterInGame(InGameGUI inGameGUI) {
        this.inGameGUI = inGameGUI;
    }

    public void checkmessage(String str) {
        String[] splitted = str.split(" ");
        String keyword = splitted[0].toUpperCase();
        if (!expectedMessage.get().equals(keyword)) {
            LoggerNetwork.error("Unexpected message: expected=" + expectedMessage + ", got=" + keyword);
        }
        switch (keyword) {
            case "SIZE":
                int playgroundSize = Integer.parseInt(splitted[1]);
                this.initPlayer(playgroundSize);
                setSentData("playgroundSize", playgroundSize);
                setSentData("isLoadGame", false);
                expectedMessage.set("CONFIRMED");
                break;
            case "SHOT":
                // +1 to ignore leading SHOT
                Position position = new Position(Integer.parseInt(splitted[SHOOT_INDEX_X + 1]) - SHOOT_OFFSET,
                        Integer.parseInt(splitted[SHOOT_INDEX_Y + 1]) - SHOOT_OFFSET);
                setSentData("makeTurnPosition", position);
                // expected message will be set later on
                break;
            case "SAVE":
                long id = Long.parseLong(splitted[1]);
                inGameGUI.saveGame(id);
                break;
            case "LOAD":
                id = Long.parseLong(splitted[1]);
                setSentData("isLoadGame", true);
                setSentData("gameID", id);
                break;
            case "CONFIRMED":
                player.setAllShipsPlaced();
                if (isStartingPlayer) {
                    expectedMessage.set("SHOT"); // PlayerNetwork starts: Enemy shoots first
                } else expectedMessage.set("ANSWER");
                break;
            case "ANSWER":
                switch (splitted[1]) {
                    case "0":    // WATER
                        setSentData("shotResult", new ShotResTuple(Playground.FieldType.WATER, false));
                        expectedMessage.set("SHOT");
                        sendMessage("pass");
                        break;
                    case "1":  // SHIP
                        setSentData("shotResult", new ShotResTuple(Playground.FieldType.SHIP, false));
                        expectedMessage.set("ANSWER");
                        break;
                    case "2":  // SHIP SUNKEN
                        setSentData("shotResult", new ShotResTuple(Playground.FieldType.SHIP, true));
                        expectedMessage.set("ANSWER");
                        break;
                }
                break;
            case "PASS":
                expectedMessage.set("ANSWER");
                break;
            default:
                LoggerNetwork.error("Unrecognized keyword: keyword=" + keyword);
        }

    }


    /**
     * Start listening
     */
    protected void waitMessage() {
        this.isRunning = true;
        LoggerNetwork.info("Start listening to new messages");
        this.expectedMessage.set(expectedFirstMessage);
        while (isRunning) {
            try {
                String answer = in.readLine();
                LoggerNetwork.debug("New message: " + answer);
                synchronized (expectedMessage) {
                    checkmessage(answer);
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (!socket.isConnected()) {
                    this.isRunning = false;
                    LoggerNetwork.info("Stop listening to messages. Reason: Disconnected from other side");
                }
            }
        }
        LoggerNetwork.info("End waitMessage loop");
    }

    public void sendStartGame(int playgroundSize) {
        sendMessage("size " + playgroundSize);
    }

    public void sendAnswer(ShotResult res) {
        String num;
        if (res.getType() == Playground.FieldType.SHIP) {
            if (((ShotResultShip) res).getStatus() == Ship.LifeStatus.SUNKEN) {
                num = "2";
            } else {
                num = "1";
            }
            expectedMessage.set("SHOT");
        } else {
            num = "0";
            expectedMessage.set("ANSWER");
        }
        sendMessage("answer " + num);
    }

    public void sendSaveGame(long id) {
        sendMessage("save " + id);
    }

    public void sendShot(Position pos) {
        int[] sendPos = new int[2];
        sendPos[SHOOT_INDEX_X] = pos.getX() + SHOOT_OFFSET;
        sendPos[SHOOT_INDEX_Y] = pos.getY() + SHOOT_OFFSET;
        sendMessage("shot " + sendPos[0] + " " + sendPos[1]);
    }

    public void sendLoadGame(long gameID) {
        sendMessage("load " + gameID);
    }

    private void sendMessage(String message) {
        if (message.equals("answer 0")) {
            synchronized (expectedMessage) {
                expectedMessage.set("PASS");
            }
        }
        try {
            out.write(message + "\n");
            out.flush();
            LoggerNetwork.debug("[ " + getClass().getSimpleName() + " ] Sent message: " + message);
        } catch (IOException e) {
            LoggerNetwork.error("Can't send message");
            e.printStackTrace();
        }
    }

    public void stopListening() {
        this.isRunning = false;
    }

    protected void initPlayer(int playgroundSize) {
        this.player = new PlayerNetwork(1, "Network", playgroundSize, this);
    }

    /**
     * blocking till available
     *
     * @return The playground size, sent by the server
     */
    public int getPlaygroundSize() {
        Object o = popSentData("playgroundSize");
        assert o instanceof Integer : "Wrong data sent. Expected Integer got " + o;
        return (int) o;
    }

    /**
     * blocking till available
     *
     * @return The last sent ShotResult
     */
    public ShotResTuple getShotResult() {
        Object o = popSentData("shotResult");
        assert o instanceof ShotResTuple : "Wrong data sent. Expected FieldType got " + o;
        return (ShotResTuple) o;
    }

    /**
     * blocking till available
     *
     * @return The last sent shot target
     */
    public Position popMakeTurnPosition() {
        Object o = popSentData("makeTurnPosition");
        assert o instanceof Position : "Wrong data sent. Expected Position got " + o;
        return (Position) o;
    }

    /**
     * blocking till available
     *
     * @return true if the Game is created from a previous save game
     */
    public boolean isLoadGame() {
        Object o = popSentData("isLoadGame");
        assert o instanceof Boolean : "Wrong data stored. Expected Boolean got " + o;
        return (Boolean) o;
    }

    /**
     * blocking till available
     *
     * @return The ID of the game if it is a load game
     */
    public long getGameID() {
        Object o = popSentData("gameID");
        assert o instanceof Long : "Wrong data stored. Expected long got " + o;
        return (Long) o;
    }


    private void setSentData(String key, Object o) {
        synchronized (sentData) {
            sentData.put(key, o);
            sentData.notifyAll();
        }
    }

    private Object getSentData(String key) {
        return getSentData(key, false);
    }

    private Object popSentData(String key) {
        return getSentData(key, true);
    }

    private Object getSentData(String key, boolean remove) {
        synchronized (this.sentData) {
            while (!this.sentData.containsKey(key)) {
                try {
                    this.sentData.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LoggerNetwork.warning("Interrupted getSentDat thread: returning null");
                    return null;
                }
            }
            if (remove) {
                return this.sentData.remove(key);
            } else {
                return this.sentData.get(key);
            }
        }
    }


    public static class ShotResTuple {

        public Playground.FieldType type;
        public boolean sunken;

        public ShotResTuple(Playground.FieldType type, boolean sunken) {
            this.type = type;
            this.sunken = sunken;
        }

    }

    /**
     * When loaded game
     *
     * @param isStartingPlayer
     */
    public void setIsStartingPlayerOnLoad(boolean isStartingPlayer) {
        this.isStartingPlayer = isStartingPlayer;
        if (isStartingPlayer) {
            expectedMessage.set("SHOT"); // PlayerNetwork starts: Enemy shoots first
        } else expectedMessage.set("ANSWER");
    }

    // Thread control
    // TODO: shutdown with ResourceDestructor


    // GETTER

    /**
     * @return The Player Object that was at {@link #initPlayer(int)} method
     */
    public PlayerNetwork getPlayerNetwork() {
        assert player != null : "player has not been initialized yet. Wait till initPlayer method was called. After " +
                "recv size or send size";
        return player;
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    public boolean isStarted() {
        return isStarted;
    }
}
