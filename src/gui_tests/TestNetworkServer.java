package gui_tests;

import javafx.application.Application;
import javafx.stage.Stage;
import network.Server;

public class TestNetworkServer extends Application {
    @Override
    public void start(Stage stage) {
        int playgroundSize = 10;
        // Start server
        Server server = new Server(50000);
        server.start();
        // wait for client
        server.waitTillClientConnected();
        // start game -> ShipPlacement
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        server.startGame(playgroundSize);
        server.startCommunication();
    }
}
