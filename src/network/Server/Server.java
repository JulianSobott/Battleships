package network.Server;

import core.utils.logging.LoggerLogic;
import core.utils.logging.LoggerNetwork;
import gui.PlayGame.ControllerPlayGame;
import network.Connected;
import network.ConnectionStatus;
import network.Utils;
import player.PlayerNetwork;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class Server extends Connected {

    private ServerSocket socket;
    private Socket clientSocket;
    private final AtomicBoolean clientConnected = new AtomicBoolean(false);

    public Server(int port) {
        this.expectedFirstMessage = "CONFIRMED";
        this.isStartingPlayer = false;
        try {
            socket = new ServerSocket(port);
            LoggerNetwork.info("Start server: ip=" + Utils.getIpAddress() + ", port=" + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ConnectionStatus start() {
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

            } catch (SocketException e){
                LoggerNetwork.info("Server accept interrupted. cause=" + e.getMessage());
            } catch(IOException e) {
                e.printStackTrace();
            }
        }).start();
        isStarted = true;
        return ConnectionStatus.SUCCESSFUL;
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

    public void startGame(int size){
        sendStartGame(size);
        this.initPlayer(size);
    }

    public void shutdown() {
        try {
            if (socket != null) {
                socket.close();
                stopListening();
                LoggerNetwork.info("Closed Server");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
