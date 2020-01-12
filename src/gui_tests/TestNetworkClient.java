package gui_tests;

import javafx.application.Application;
import javafx.stage.Stage;
import network.Client.Client;


public class TestNetworkClient extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Connect to server
        Client client = new Client("localhost", 5000);
        client.start();
        client.startCommunication();
    }
}
