package Server;

import DataStructures.LinkedList;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread{
    private int port;

    private LinkedList<UserManager> userList = new LinkedList<UserManager>();

    public Server(int port){
        this.port = port;
    }

    @Override
    public void run(){
        try{
            ServerSocket serverSocket = new ServerSocket(port);

            while(true){
                Socket clientSocket = serverSocket.accept();
                System.out.println("Usuario registrado");

                UserManager user = new UserManager(this, clientSocket);
                userList.add(user);
                user.start();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public LinkedList<UserManager> getUserList(){
        return userList;
    }

    public void removeUser(UserManager userManager){
        userList.remove(userManager);
    }
}
