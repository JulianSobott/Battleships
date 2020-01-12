package network.Server;

import core.utils.logging.LoggerNetwork;
import gui.PlayGame.ControllerPlayGame;
import network.Connected;
import network.Utils;
import player.PlayerNetwork;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class Server extends Connected {

    private ServerSocket socket;
    private Socket clientSocket;
    private final AtomicBoolean clientConnected = new AtomicBoolean(false);

    public Server(int port) {
        try {
            socket = new ServerSocket(port);
            LoggerNetwork.info("Start server: ip=" + Utils.getIpAddress() + ", port=" + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        new Thread(() -> {
            LoggerNetwork.info("Server starts accepting in new thread");
            try {
                clientSocket = socket.accept();
                super.connected(clientSocket);
                synchronized (clientConnected) {
                    clientConnected.set(true);
                    clientConnected.notify();
                }
                LoggerNetwork.info("New Client connected: " + clientSocket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void waitTillClientConnected() {
        synchronized (clientConnected) {
            while (!clientConnected.get()) {
                try {
                    clientConnected.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        assert clientSocket != null;
    }

    @Override
    public void startCommunication() {
        this.waitMessage("CONFIRMED");
    }
}
