package network.Client;

import network.Connected;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client extends Connected {

    private Socket socket;
    public Client(String ip) throws IOException {
        socket = new Socket(ip, 50000);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // empfang
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); // output Netzwerk
    }




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
