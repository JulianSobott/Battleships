package network;

import core.*;
import core.communication_data.Position;

public class Connected {

    // Todo evtl in einzelne Methoden für Server und Client aufteilen ???


    public static String checkmessage(String str) {

        String[] splitted = str.split(" ");
        String keyword = splitted[0];
        switch (keyword) {
            case "SIZE":
                int size = Integer.parseInt(splitted[1]);
                PlaygroundOwn playgroundOwn = new PlaygroundOwn(size);
                PlaygroundEnemy playgroundEnemy = new PlaygroundEnemy(size);
                return "Confirmed";
            case "SHOT":
                Position shot = new Position(Integer.parseInt(splitted[1]), Integer.parseInt(splitted[2])); // war nicht irgendwo was, wo man umgekehrt dachten musste und x und y vertauschen musste?
                return "b";
            case "SAVE":
                return "c";
            case "LOAD":
                return "d";
            case "SHIPS":
                    // Unnötig da Size ja schon sagt wie viele schiffe?
            case "Confirmed":
                return "xyz"; // Server muss confirmed nur einmal machen? Mitzählen?
            case "Answer":
                if (splitted[1].equals("0")){

                } else if (splitted[1].equals("1")){

                } else if (splitted[1].equals("2")) {

                }
            default:
                return "x";
        }

    }

}
