package Server;

import DataStructures.BinaryTree;
import DataStructures.LinkedList;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread{
    private int port;

    private LinkedList<UserManager> userList = new LinkedList<UserManager>();
    private BinaryTree<User> userTree = new BinaryTree<>();

    public Server(int port){
        this.port = port;
    }

    @Override
    public void run(){
        try{
            ServerSocket serverSocket = new ServerSocket(port);
            getUserTree();
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

    public void getUserTree() throws IOException {
        File archivo = new File("users.json");
        ObjectMapper mapperJson = new ObjectMapper();
        User[] users = mapperJson.readValue(archivo, User[].class);

        for (int i = 0; i < users.length; i++) {
            if (users[i] != null){
                userTree.add(users[i]);
                break;
            }
        }
    }

    public LinkedList<UserManager> getUserList(){
        return userList;
    }

    public void removeUser(UserManager userManager){
        userList.remove(userManager);
    }
}
