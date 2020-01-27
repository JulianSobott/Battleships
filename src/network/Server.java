package network;

import core.utils.logging.LoggerNetwork;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server extends Connected {

    private ServerSocket socket;
    private Socket clientSocket;
    private final AtomicBoolean clientConnected = new AtomicBoolean(false);

    public Server(int port) {
        this.expectedFirstMessage = "CONFIRMED";
        try {
            socket = new ServerSocket(port);
            socket.setReuseAddress(true);
            LoggerNetwork.info("Start server: ip=" + Utils.getIpAddress() + ", port=" + port);
            this.isStartingPlayer = true;
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
                    LoggerNetwork.info("Server stopped waiting for client connection");
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
            isStarted = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
