package network;

import core.*;
import core.communication_data.Position;
import core.communication_data.TurnResult;
import core.utils.logging.LoggerLogic;
import core.utils.logging.LoggerNetwork;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;

public class Connected {

    // Todo evtl in einzelne Methoden für Server und Client aufteilen ???

    protected BufferedReader in = null;
    protected BufferedWriter out = null; //setzen damit immerhin null drinsteht bei voreiliger ansprache
    protected String expectedMessage;
    protected boolean isServer = false;
    protected boolean isRunning = false;

    // Data
    private final HashMap<String, Object> sentData = new HashMap<>();


    public String checkmessage(String str) {
        String[] splitted = str.split(" ");
        String keyword = splitted[0];
        switch (keyword.toUpperCase()) {
            case "SIZE":
                sentData.put("playgroundSize", Integer.parseInt(splitted[1]));
                expectedMessage="CONFIRMED";
            case "SHOT":
                Position shot = new Position(Integer.parseInt(splitted[1]), Integer.parseInt(splitted[2])); // war nicht irgendwo was, wo man umgekehrt dachten musste und x und y vertauschen musste?
                expectedMessage="ANSWER";
                return "b";
            case "SAVE":
                return "c";
            case "LOAD":
                return "d";
            case "CONFIRMED":
                if (isServer){
                    expectedMessage="SHOT";
                } else expectedMessage="CONFIRMED";
                return "xyz"; // Server muss confirmed nur einmal machen? Mitzählen?
            case "ANSWER":
                if (splitted[1].equals("0")){

                } else if (splitted[1].equals("1")){

                } else if (splitted[1].equals("2")) {

                }
            case "pass":
            default:
                return "x";
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


        return;
    }

    public void stopListening() {
        this.isRunning = false;
    }

    public int getPlaygroundSize() {
        synchronized (this.sentData) {
            while (this.sentData.get("playgroundSize") != null) {
                try {
                    this.sentData.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LoggerNetwork.warning("Interrupted getPlaygroundSize thread: returning -1");
                    return -1;
                }
            }
            assert this.sentData.get("playgroundSize") instanceof Integer: "Wrong data sent";
            return (int) this.sentData.get("playgroundSize");
        }
    }
}
