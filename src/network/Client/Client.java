package network.Client;

import core.utils.logging.LoggerNetwork;
import gui.PlayGame.ControllerPlayGame;
import network.Connected;
import player.PlayerNetwork;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client extends Connected {

    private Socket socket;
    private String ip;
    private int port;

    public Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void start() {
        try {
            socket = new Socket(ip, port);
            super.connected(socket);
            LoggerNetwork.info("Client connected to server: ip=" + ip + ", port=" + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startCommunication() {
        this.waitMessage("SIZE");
    }
}
