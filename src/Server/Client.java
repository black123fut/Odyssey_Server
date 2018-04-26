package Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Esta clase solo es para probar el servidor sera eliminada despues.
 */
public class Client {
    private  int port;

    private Socket socket;
    private InputStream serverIn;
    private PrintWriter serverOut;

    public static void main(String[] args) {
        new Client(8000);
    }

    public Client(int port){
        this.port = port;
        run();
    }

    public void run(){
        try {
            socket = new Socket("192.168.100.6", port);

            for (int i = 0; i < 5; i++) {
                Scanner scan = new Scanner(System.in);
                System.out.println("new message");

                String toSend = scan.nextLine();
                sendMessage("user: " + toSend);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String cmd){
        try {
            serverOut = new PrintWriter(socket.getOutputStream(), true);
            serverOut.println(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
