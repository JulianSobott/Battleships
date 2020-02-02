package network;

import core.utils.logging.LoggerNetwork;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends Connected {

    private Socket socket;
    private String ip;
    private int port;

    public Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.expectedFirstMessage = "SIZE";
        this.isStartingPlayer = false;
    }

    @Override
    public ConnectionStatus start() {
        try {
            socket = new Socket(ip, port);
            super.connected(socket);
            isStarted = true;
            LoggerNetwork.info("Client connected to server: ip=" + ip + ", port=" + port);
            return ConnectionStatus.SUCCESSFUL;
        } catch (UnknownHostException e) {
            LoggerNetwork.warning("Unknown host: ip=" + ip + ", port=" + port);
            return ConnectionStatus.UNKNOWN_HOST;
        } catch (ConnectException e) {
            LoggerNetwork.warning("Network connection refused: ip=" + ip + ", port=" + port);
            return ConnectionStatus.REFUSED;
        } catch(IOException e) {
            LoggerNetwork.warning("Network is unreachable: ip=" + ip + ", port=" + port);
            return ConnectionStatus.UNKNOWN_HOST;
        }
    }
}
