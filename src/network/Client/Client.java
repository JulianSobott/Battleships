package network.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        try {
            Socket s = new Socket("localhost", 4999);
//            PrintWriter pr = new PrintWriter(s.getOutputStream());
//            pr.println("is it working");
//            pr.flush();
//
//            InputStreamReader in = new InputStreamReader(s.getInputStream());
//            BufferedReader bf = new BufferedReader(in);
//
//            String str = bf.readLine();
//            System.out.println("Server: " + str);

            //
            //

            while (true) {

                PrintWriter pr = new PrintWriter(s.getOutputStream());
                Scanner scanner = new Scanner(System.in);
                String nextline = scanner.nextLine();
                pr.println(nextline);
                pr.flush();

                InputStreamReader in = new InputStreamReader(s.getInputStream());
                BufferedReader bf = new BufferedReader(in);

                String str = bf.readLine();
                System.out.println("[Server]: " + str);
            }

            //
            //

        } catch (IOException e) {
            e.printStackTrace();
        }




    }
}
