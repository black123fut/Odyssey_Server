package Server;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Text;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Esta clase solo es para probar el servidor sera eliminada despues.
 */
public class Client {
    private  int port;

    private Socket socket;
    private InputStream serverIn;
    private OutputStream serverOut;
    private Document message;

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
            serverOut = socket.getOutputStream();


            sendMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(){
        message = new Document();

        Element root = new Element("message");
        message.setRootElement(root);

        Element command = new Element("command");
        command.addContent(new Text("Registrar"));

        Element data = new Element("data");

        Element userName = new Element("username");
        userName.addContent(new Text("Nor")); //Variable

        Element name = new Element("name");
        name.addContent(new Text("Isaac")); //Variable

        Element surname = new Element("surname");
        surname.addContent(new Text("Benavides")); //Variable

        Element age = new Element("age");
        age.addContent(new Text("19")); //Variable

        Element password = new Element("password");
        password.addContent(new Text("hoplo")); //Variable

        data.addContent(name);
        data.addContent(surname);
        data.addContent(age);
        data.addContent(password);

        root.addContent(command);
        root.addContent(data);

        System.out.println(message.getRootElement().getChildText("command"));

        try {
            serverOut.write(message.toString().getBytes());
            serverOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}






















