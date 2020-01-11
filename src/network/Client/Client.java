package network.Client;

import gui.PlayGame.ControllerPlayGame;
import network.Connected;
import player.PlayerNetwork;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client extends Connected {
    public Client(PlayerNetwork player, ControllerPlayGame controllerPlayGame) {
        super(controllerPlayGame);
    }

    @Override
    public void start() {

    }

    //    private Socket socket;
//    public Client(String ip) {
//        super();
//        socket = new Socket(ip, 50000);
//        in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // empfang
//        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); // output Netzwerk
//    }

}
