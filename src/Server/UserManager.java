package Server;

import org.jdom2.JDOMException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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
            String xml = reader.readLine();

            Document message = stringToXml(xml);

            if (message != null){
                message.getDocumentElement().normalize();
                NodeList nodeList = message.getElementsByTagName("command");

                if (nodeList.item(0).getTextContent().equalsIgnoreCase("registrar")){
                    registar(message);
                }
            }
            clientSocket.close();

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void registar(Document message){
        NodeList dataList = message.getElementsByTagName("data");
        Element data = (Element) dataList.item(0);

        System.out.println("Username: " + data.getElementsByTagName("username").item(0).getTextContent());
        System.out.println("Name: " + data.getElementsByTagName("name").item(0).getTextContent());
        System.out.println("Surname: " + data.getElementsByTagName("surname").item(0).getTextContent());
        System.out.println("Age: " + data.getElementsByTagName("age").item(0).getTextContent());

    }

    private Document stringToXml(String xmlStr) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            return builder.parse( new InputSource(new StringReader(xmlStr)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}











