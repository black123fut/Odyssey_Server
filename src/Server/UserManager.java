package Server;

import java.io.*;
import java.net.Socket;

public class UserManager extends Thread{
    private Socket clientSocket;
    private Server server;
    private OutputStream outputStream;

    public UserManager(Server server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run(){
        try{
            InputStream inputStream = clientSocket.getInputStream();
            this.outputStream = clientSocket.getOutputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while((line = reader.readLine()) != null) {
                System.out.println(line);
                if (line.equalsIgnoreCase("Registrar")){

                }

            }

            clientSocket.close();

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void signInUser(){

    }

}
