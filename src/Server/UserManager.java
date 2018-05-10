package Server;

import Mensajes.ErrorMessage;
import Mensajes.Message;
import Mensajes.SignInMessage;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.Socket;


public class UserManager extends Thread{
    private Socket clientSocket;
    private Server server;
    private InputStream inputStream;
    private PrintWriter outputStream;

    public UserManager(Server server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run(){
        try{
            outputStream = new PrintWriter(clientSocket.getOutputStream(), true);
            inputStream = clientSocket.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            XmlMapper mapper = new XmlMapper();

            String xml;
            while((xml = reader.readLine()) != null){
                Message mensaje = mapper.readValue(xml, Message.class);

                if (mensaje.getOpcode().equalsIgnoreCase("registrar")){
                    Message<SignInMessage> mail = mapper.readValue(xml, new TypeReference<Message<SignInMessage>>() {});
                    registar(mail);
                }
            }

            clientSocket.close();

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void reproducir(){
        try{
            File path = new File("src/music/musicc.wav");
            FileInputStream file = new FileInputStream(path);

            byte[] buffer = inputStreamToByteArray(file);


            //outputStream.write(buffer);
//            byte[] buffer = new byte[(int) path.length()];

//            file.read(buffer);


        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public byte[] inputStreamToByteArray(InputStream inputStream) throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int bytesRead;
        while((bytesRead = inputStream.read(buffer)) > 0){
            baos.write(buffer, 0, bytesRead);
        }
        return baos.toByteArray();
    }

    private void registar(Message<SignInMessage> message) throws IOException{
        File archivo = new File("users.json");
        ObjectMapper mapperJson = new ObjectMapper();
        User[] users = mapperJson.readValue(archivo, User[].class);

        for (User x: users) {
            if (x == null){
                break;
            }
            else if (x.getUsername().equals(message.getData().getUsername())){
                Message<ErrorMessage> errorMessage = new Message<>();
                errorMessage.setOpcode("444");

                ErrorMessage error = new ErrorMessage();
                error.setError("Nombre de usuario ya existe");

                errorMessage.setData(error);
                XmlMapper mapper2 = new XmlMapper();

                send(mapper2.writeValueAsString(errorMessage));
                System.out.println("Nombre de usuario ya existe");
                return;
            }
        }

        for (int i = 0; i < users.length; i++) {
            if (users[i] == null){
                users[i] = new User(message.getData().getUsername(), message.getData().getName()
                        , message.getData().getSurname(), message.getData().getAge());
                break;
            }
        }
        mapperJson.writeValue(archivo, users);
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

    public void send(String message){
        try {
            outputStream.println(message);
            outputStream.flush();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}