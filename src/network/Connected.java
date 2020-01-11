package network;

import core.*;
import core.communication_data.Position;

import java.io.*;
import java.net.Socket;

public class Connected {

    // Todo evtl in einzelne Methoden für Server und Client aufteilen ???

    protected BufferedReader in = null;
    protected BufferedWriter out = null; //setzen damit immerhin null drinsteht bei voreiliger ansprache
    protected String expectedMessage;
    protected boolean isServer = false;

    public String checkmessage(String str) {
        String[] splitted = str.split(" ");
        String keyword = splitted[0];
        switch (keyword.toUpperCase()) {
            case "SIZE":
                int size = Integer.parseInt(splitted[1]);
                PlaygroundOwn playgroundOwn = new PlaygroundOwn(size);
                PlaygroundEnemy playgroundEnemy = new PlaygroundEnemy(size);

                expectedMessage="Confirmed";
                return "Confirmed";
            case "SHOT":
                Position shot = new Position(Integer.parseInt(splitted[1]), Integer.parseInt(splitted[2])); // war nicht irgendwo was, wo man umgekehrt dachten musste und x und y vertauschen musste?
                expectedMessage="Answer";
                return "b";
            case "SAVE":
                return "c";
            case "LOAD":
                return "d";
            case "Confirmed":
                if (isServer){
                    expectedMessage="Shot";
                } else expectedMessage="confirmed";
                return "xyz"; // Server muss confirmed nur einmal machen? Mitzählen?
            case "Answer":
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
        pr.println("SIZE " + size);
        pr.flush();
        waitMessage();
    }
    public void waitMessage(){
        while (true) {
            try {
                String answer = in.readLine();
                checkmessage(answer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message){


        return;
    }


}
