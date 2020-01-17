package network;

import core.communication_data.Position;
import core.playgrounds.Playground;
import core.utils.logging.LoggerNetwork;
import gui.PlayGame.InGameGUI;
import gui.interfaces.Shutdown;
import player.PlayerNetwork;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public abstract class Connected implements Shutdown {

    // Todo evtl in einzelne Methoden für Server und Client aufteilen ???
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
                expectedMessage.set("CONFIRMED");
                break;
            case "SHOT":
                Position position = new Position(Integer.parseInt(splitted[1]), Integer.parseInt(splitted[2]));
                setSentData("makeTurnPosition", position);
                expectedMessage.set("ANSWER");
                break;
            case "SAVE":
                long id = Long.parseLong(splitted[1]);
                inGameGUI.saveGame(id);
                break;
            case "LOAD":
                break;
            case "CONFIRMED":
                player.setAllShipsPlaced();
                if (isStartingPlayer){
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
                expectedMessage.set("SHOT");
                break;
            default:
                LoggerNetwork.error("Unrecognized keyword: keyword=" + keyword);
        }

    }


    /**
     * Start listening
     */
    protected void waitMessage(){
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

    public void sendMessage(String message){
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
     * @return
     */
    public int getPlaygroundSize() {
        Object o = popSentData("playgroundSize");
        assert o instanceof Integer: "Wrong data sent. Expected Integer got " + o;
        return (int) o;
    }

    /**
     * blocking till available
     * @return
     */
    public ShotResTuple getShotResult() {
        Object o = popSentData("shotResult");
        assert o instanceof ShotResTuple: "Wrong data sent. Expected FieldType got " + o;
        return (ShotResTuple) o;
    }

    /**
     * blocking till available
     * @return
     */
    public Position popMakeTurnPosition() {
        Object o = popSentData("makeTurnPosition");
        assert o instanceof Position: "Wrong data sent. Expected Position got " + o;
        return (Position) o;
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

    // Thread control

    @Override
    public void onShutdown() {
        isRunning = false;
    }


    // GETTER
    /**
     *
     * @return The Player Object that was at {@link #initPlayer(int)} method
     */
    public PlayerNetwork getPlayerNetwork() {
        assert player != null: "player has not been initialized yet. Wait till initPlayer method was called. After " +
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
