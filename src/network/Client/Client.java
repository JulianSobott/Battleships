package network.Client;

import network.Connected;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client extends Connected {

    public static void main(String[] args) {
        try {
            Socket s = new Socket("localhost", 50000);

            InputStreamReader in1 = new InputStreamReader(s.getInputStream());
            BufferedReader bf1 = new BufferedReader(in1);
            String str1 = bf1.readLine();
            Connected.checkmessage(str1);
            System.out.println("[Server]: " + str1);

            while (true) {

                PrintWriter pr = new PrintWriter(s.getOutputStream());
                Scanner scanner = new Scanner(System.in);
                String nextline = scanner.nextLine();
                pr.println(nextline);
                pr.flush();

                InputStreamReader in = new InputStreamReader(s.getInputStream());
                BufferedReader bf = new BufferedReader(in);
                String str = bf.readLine();
                Connected.checkmessage(str);
                System.out.println(checkmessage(str));
                System.out.println("[Server]: " + str);
            }

            //
            //

        } catch (IOException e) {
            e.printStackTrace();
        }




    }


}
