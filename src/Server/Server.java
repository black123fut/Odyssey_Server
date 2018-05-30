package Server;

import DataStructures.BinaryTree;
import DataStructures.LinkedList;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread{
    private int port;

    private LinkedList<UserManager> userList = new LinkedList<>();
    private BinaryTree<User> userTree;

    /**
     * Constructor.
     * @param port Numero de puerto.
     */
    public Server(int port){
        this.port = port;
    }

    /**
     * Corre el thread del servidor.
     */
    @Override
    public void run(){
        try{
            ServerSocket serverSocket = new ServerSocket(port);
            updateUserTree();
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

    /**
     * Actualiza el arbol binario de los usuarios.
     */
    public void updateUserTree() throws IOException {
        File archivo = new File("users.json");
        ObjectMapper mapperJson = new ObjectMapper();
        User[] users = mapperJson.readValue(archivo, User[].class);
        userTree = new BinaryTree<>();
        //guarda los usuarios en el arbol binario.
        for (int i = 0; i < users.length; i++) {
            userTree.add(users[i]);
        }
    }

    public LinkedList<UserManager> getUserList(){
        return userList;
    }

    public void removeUser(UserManager userManager){
        userList.remove(userManager);
    }
}
