package network.Server;

import network.Connected;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server extends Connected {

    public static void main(String[] args) {
        ServerSocket ss;
        try {
            ss = new ServerSocket(50000);
            Socket s = ss.accept();
            System.out.println("client connected");

            PrintWriter printWriter1 = new PrintWriter(s.getOutputStream());
            Scanner scanner1 = new Scanner(System.in);
            String nextline1 = scanner1.nextLine();
            printWriter1.println(nextline1);
            printWriter1.flush();

            while (true) {

                InputStreamReader in = new InputStreamReader(s.getInputStream());
                BufferedReader bf = new BufferedReader(in);
                String str = bf.readLine();
                System.out.println(checkmessage(str));
                System.out.println("[Client]: " + str);
                PrintWriter printWriter = new PrintWriter(s.getOutputStream());
                Scanner scanner = new Scanner(System.in);
                String nextline = scanner.nextLine();
                printWriter.println(nextline);
                printWriter.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



}
