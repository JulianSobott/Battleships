package network;

import core.*;
import core.communication_data.Position;
import core.communication_data.TurnResult;
import core.serialization.GameSerialization;
import core.utils.logging.LoggerLogic;
import core.utils.logging.LoggerNetwork;
import gui.PlayGame.ControllerPlayGame;
import player.PlayerNetwork;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public abstract class Connected {

    // Todo evtl in einzelne Methoden für Server und Client aufteilen ???

    protected BufferedReader in = null;
    protected BufferedWriter out = null; //setzen damit immerhin null drinsteht bei voreiliger ansprache
    private Socket socket;
    protected boolean isStartingPlayer = false;
    protected final AtomicReference<String> expectedMessage = new AtomicReference<>();
    protected boolean isRunning = false;
    protected PlayerNetwork player;

    // Data
    private final HashMap<String, Object> sentData = new HashMap<>();
    private ControllerPlayGame controllerPlayGame;


    public abstract void start();

    public abstract void startCommunication();

    public void connected(Socket socket) {
        this.socket = socket;
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void checkmessage(String str) {
        String[] splitted = str.split(" ");
        String keyword = splitted[0].toUpperCase();
        if (!expectedMessage.get().equals(keyword)) {
            LoggerNetwork.error("Unexpected message: expected=" + expectedMessage + ", got=" + keyword);
        }
        switch (keyword) {
            case "SIZE":
                sentData.put("playgroundSize", Integer.parseInt(splitted[1]));
                expectedMessage.set("CONFIRMED");
                break;
            case "SHOT":
                Position position = new Position(Integer.parseInt(splitted[1]), Integer.parseInt(splitted[2]));
                sentData.put("makeTurnPosition", position);
                expectedMessage.set("ANSWER");
                break;
            case "SAVE":
                String id = splitted[1];
                // TODO: pass ID to GUI
                controllerPlayGame.clickSaveGame();
                break;
            case "LOAD":
                break;
            case "CONFIRMED":
                player.setAllShipsPlaced();
                if (isStartingPlayer){
                    expectedMessage.set("ANSWER");
                } else expectedMessage.set("SHOT");
                break;
            case "ANSWER":
                switch (splitted[1]) {
                    case "0":    // WATER
                        sentData.put("shotResult", new ShotResTuple(Playground.FieldType.WATER, false));
                        expectedMessage.set("SHOT");
                        sendMessage("pass");
                        break;
                    case "1":  // SHIP
                        sentData.put("shotResult", new ShotResTuple(Playground.FieldType.SHIP, false));
                        expectedMessage.set("ANSWER");
                        break;
                    case "2":  // SHIP SUNKEN
                        sentData.put("shotResult", new ShotResTuple(Playground.FieldType.SHIP, true));
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

    public void startGame(int size){
        this.sendMessage("size " + size);
    }

    /**
     * Start listening
     */
    protected void waitMessage(String expectedFirstMessage){
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

    /**
     * blocking till available
     * @return
     */
    public int getPlaygroundSize() {
        Object o = getSentData("playgroundSize");
        assert o instanceof Playground.FieldType: "Wrong data sent. Expected int got " + o;
        return (int) o;
    }

    /**
     * blocking till available
     * @return
     */
    public ShotResTuple getShotResult() {
        Object o = getSentData("shotResult");
        assert o instanceof ShotResTuple: "Wrong data sent. Expected FieldType got " + o;
        return (ShotResTuple) o;
    }

    /**
     * blocking till available
     * @return
     */
    public Position getMakeTurnPosition() {
        Object o = getSentData("makeTurnPosition");
        assert o instanceof Playground.FieldType: "Wrong data sent. Expected Position got " + o;
        return (Position) o;
    }

    private Object getSentData(String key) {
        synchronized (this.sentData) {
            while (this.sentData.get(key) != null) {
                try {
                    this.sentData.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LoggerNetwork.warning("Interrupted getSentDat thread: returning null");
                    return null;
                }
            }
            return this.sentData.get(key);
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
}
