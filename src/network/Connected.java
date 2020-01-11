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

public abstract class Connected {

    // Todo evtl in einzelne Methoden für Server und Client aufteilen ???

    protected BufferedReader in = null;
    protected BufferedWriter out = null; //setzen damit immerhin null drinsteht bei voreiliger ansprache
    protected String expectedMessage;
    protected boolean isStartingPlayer = false;
    protected boolean isRunning = false;
    protected PlayerNetwork player;

    // Data
    private final HashMap<String, Object> sentData = new HashMap<>();
    private ControllerPlayGame controllerPlayGame;

    public Connected(ControllerPlayGame controllerPlayGame) {
        this.controllerPlayGame = controllerPlayGame;
    }

    public abstract void start();

    public void connected(BufferedReader in, BufferedWriter out) {
        this.in = in;
        this.out = out;
    }

    public void checkmessage(String str) {
        String[] splitted = str.split(" ");
        String keyword = splitted[0].toUpperCase();
        if (!expectedMessage.equals(keyword)) {
            LoggerNetwork.error("Unexpected message: expected=" + expectedMessage + ", got=" + keyword);
        }
        switch (keyword) {
            case "SIZE":
                sentData.put("playgroundSize", Integer.parseInt(splitted[1]));
                expectedMessage="CONFIRMED";
                break;
            case "SHOT":
                Position position = new Position(Integer.parseInt(splitted[1]), Integer.parseInt(splitted[2]));
                sentData.put("makeTurnPosition", position);
                expectedMessage="ANSWER";
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
                    expectedMessage="ANSWER";
                } else expectedMessage="SHOT";
                break;
            case "ANSWER":
                switch (splitted[1]) {
                    case "0":    // WATER
                        sentData.put("shotResult", new ShotResTuple(Playground.FieldType.WATER, false));
                        expectedMessage = "SHOT";
                        sendMessage("pass");
                        break;
                    case "1":  // SHIP
                        sentData.put("shotResult", new ShotResTuple(Playground.FieldType.SHIP, false));
                        expectedMessage = "ANSWER";
                        break;
                    case "2":  // SHIP SUNKEN
                        sentData.put("shotResult", new ShotResTuple(Playground.FieldType.SHIP, true));
                        expectedMessage = "ANSWER";
                        break;
                }
                break;
            case "PASS":
                expectedMessage = "SHOT";
                break;
            default:
                LoggerNetwork.error("Unrecognized");
        }

    }

    public void startGame(int size){
//        pr.println("SIZE " + size);
//        pr.flush();
//        waitMessage();
    }

    /**
     * Start listening
     */
    public void waitMessage(){
        this.isRunning = true;
        LoggerNetwork.info("Start listening to new messages");
        while (isRunning) {
            try {
                String answer = in.readLine();
                LoggerNetwork.debug("New message: " + answer);
                checkmessage(answer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        LoggerNetwork.info("End waitMessage loop");
    }

    public void sendMessage(String message){
        if (message.equals("answer 0")) {
            expectedMessage = "PASS";
        }
        try {
            out.write(message);
            out.flush();
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
