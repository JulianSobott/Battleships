package network.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {

    public static void main(String[] args) {
        ServerSocket ss;
        try {
            ss = new ServerSocket(4999);
            Socket s = ss.accept();
            System.out.println("client connected");
//            InputStreamReader in = new InputStreamReader(s.getInputStream());
//            BufferedReader bf = new BufferedReader(in);
//            String str = bf.readLine();

//            System.out.println("client: " + str);
//            PrintWriter printWriter = new PrintWriter(s.getOutputStream());
//            printWriter.println("yes");
//            printWriter.flush();

            //
            //

            while (true){
                InputStreamReader in = new InputStreamReader(s.getInputStream());
                BufferedReader bf = new BufferedReader(in);
                String str = bf.readLine();

                System.out.println("[Client]: " +  str);
                PrintWriter printWriter = new PrintWriter(s.getOutputStream());
                Scanner scanner = new Scanner(System.in);
                String nextline = scanner.nextLine();
                printWriter.println(nextline);
                printWriter.flush();
            }

            //
            //

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
