package Server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
            XmlMapper mapper = new XmlMapper();

            String xml = reader.readLine();
            Message mensaje = mapper.readValue(xml, SignInMessage.class);

            Document message = stringToXml(xml);

            if (message != null){
                message.getDocumentElement().normalize();

                if (mensaje.getOpcode().equalsIgnoreCase("registrar")){
                    registar(mensaje);
                }
            }
            clientSocket.close();

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void reproducir(){
        try{
            File path = new File("src/music/musica.mp3");
            byte[] buffer = new byte[(int) path.length()];

            FileInputStream file = new FileInputStream(path);
            file.read(buffer);

        } catch(IOException e){
            e.printStackTrace();
        }
    }

    private void registar(Message message) throws IOException{
        File archivo = new File("users.json");
        ObjectMapper mapperJson = new ObjectMapper();
        User[] users = mapperJson.readValue(archivo, User[].class);

        SignInMessage data = message.getData();

        for (User x: users) {
            if (x == null){
                break;
            }
            else if (x.getUsername().equals(data.getUsername())){
                System.out.println("Nombre de usuario ya existe");
                return;
            }
        }

        for (int i = 0; i < users.length; i++) {
            if (users[i] == null){
                users[i] = new User(data.getUsername(), data.getName(), data.getSurname(), data.getAge());
                break;
            }
        }
        mapperJson.writeValue(archivo, users);


        System.out.println("Username: " + message.getData().getUsername());
        System.out.println("User: " + message.getData().getName());
        System.out.println("Surname: " + message.getData().getUsername());
        System.out.println("Age: " + message.getData().getAge());

//        NodeList dataList = message.getElementsByTagName("Data");
//        Element data = (Element) dataList.item(0);
//
//        System.out.println("Username: " + data.getElementsByTagName("username").item(0).getTextContent());
//        System.out.println("Name: " + data.getElementsByTagName("name").item(0).getTextContent());
//        System.out.println("Surname: " + data.getElementsByTagName("surname").item(0).getTextContent());
//        System.out.println("Age: " + data.getElementsByTagName("age").item(0).getTextContent());

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