package gui_tests;

import core.GameManager;
import core.Player;
import gui.PlayGame.ControllerPlayGame;
import gui.UiClasses.BattleShipGui;
import javafx.application.Application;
import javafx.stage.Stage;
import network.Server.Server;
import player.PlayerNetwork;

import java.util.ArrayList;

public class TestNetworkServer extends Application {
    @Override
    public void start(Stage stage) {
        int playgroundSize = 10;
        // Start server
        Server server = new Server(5000);
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
        server.startCommunication(null);
    }
}
