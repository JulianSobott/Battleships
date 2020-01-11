package network.Server;

import gui.PlayGame.ControllerPlayGame;
import network.Connected;
import player.PlayerNetwork;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server extends Connected {


    public Server(PlayerNetwork player, ControllerPlayGame controllerPlayGame) {
        super(controllerPlayGame);
    }

    @Override
    public void start() {

    }
}
