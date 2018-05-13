package Server;

import Mensajes.InfoMessage;
import Mensajes.LogInMessage;
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
            //Lee cada mensaje nuevo.
            while((xml = reader.readLine()) != null){
                //Convierte el xml a un objeto Mensaje.
                Message mensaje = mapper.readValue(xml, Message.class);

                //Registra un usuario y lo escribe en el json
                if (mensaje.getOpcode().equalsIgnoreCase("000")){
                    //Convierte el xml a un Mensaje con Data de clase SignInMessage
                    Message<SignInMessage> messageInformation = mapper.readValue(xml, new TypeReference<Message<SignInMessage>>() {});
                    registar(messageInformation);
                }
                //Logea a un usuario.
                else if (mensaje.getOpcode().equalsIgnoreCase("001")){
                    //Convierte el xml a un Mensaje con Data de clase LogInMessage
                    Message<LogInMessage> messageInformation = mapper.readValue(xml, new TypeReference<Message<LogInMessage>>() {});
                    iniciarSesion(messageInformation);
                }
            }
            clientSocket.close();

        } catch (IOException e){
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

    private void iniciarSesion(Message<LogInMessage> message) throws IOException{
        File archivo = new File("users.json");
        ObjectMapper jsonMapper = new ObjectMapper();
        //Lee el archivo json de los usuarios y lo guarda en una variable.
        User[] users = jsonMapper.readValue(archivo, User[].class);
        XmlMapper xmlMapper= new XmlMapper();

        //Verifica que el usuario coincida con alguno ya registrado.
        for (User x: users) {
            if (x == null){
                break;
            } else if (x.getUsername().equalsIgnoreCase(message.getData().getUsername()) &&
                    x.getPassword().equalsIgnoreCase(message.getData().getPassword())){
                //Crea una respuesta al cliente
                Message<InfoMessage> response = writeInfoMessage("003", "Aceptado");
                //Manda la respuesta al cliente
                send(xmlMapper.writeValueAsString(response));
                return;
            }
        }
        //Crea una respuesta al cliente
        Message<InfoMessage> response = writeInfoMessage("002", "Usuario o clave son incorrectos");
        //Manda la respuesta al cliente
        send(xmlMapper.writeValueAsString(response));
    }

    private void registar(Message<SignInMessage> message) throws IOException{
        File archivo = new File("users.json");
        ObjectMapper mapperJson = new ObjectMapper();
        //Lee el archivo json de los usuarios y lo guarda en una variable.
        User[] users = mapperJson.readValue(archivo, User[].class);

        //Verifica que no exista un usuario con el mismo nombre de usuario.
        for (User x: users) {
            if (x == null){
                break;
            }
            else if (x.getUsername().equals(message.getData().getUsername())){
                //Crea el mensaje para la respuesta del cliente.
                Message<InfoMessage> errorMessage = writeInfoMessage("002", "Nombre de usuario ya existe");

                XmlMapper mapper2 = new XmlMapper();
                //Manda la respuesta al cliente.
                send(mapper2.writeValueAsString(errorMessage));
                return;
            }
        }

        //Guarda al nuevo usuario en la variable "users".
        for (int i = 0; i < users.length; i++) {
            if (users[i] == null){
                users[i] = new User(message.getData().getUsername(), message.getData().getName()
                        , message.getData().getSurname(), message.getData().getAge());

                Message<InfoMessage> response = writeInfoMessage("003", "Aceptado");

                XmlMapper mapper2 = new XmlMapper();
                send(mapper2.writeValueAsString(response));
                break;
            }
        }
        //Sobre-escribe la variable users en un json.
        mapperJson.writeValue(archivo, users);
    }

    /**
     * Escribe los mensajes con Data de tipo InfoMessage.
     * @param opcode Codigo para identificar la accion.
     * @param info Informacion de la respuesta.
     * @return Un Mensaje de informacion.
     */
    private Message<InfoMessage> writeInfoMessage(String opcode, String info){
        Message<InfoMessage> response = new Message<>();
        response.setOpcode(opcode);

        InfoMessage infoMessage = new InfoMessage();
        infoMessage.setInfo(info);

        response.setData(infoMessage);

        return response;
    }

    /**
     * Envia una respuesta al cliente.
     * @param message Xml escrito como string.
     */
    public void send(String message){
        try {
            outputStream.println(message);
            outputStream.flush();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Transforma una string a xml.
     * @param xmlStr string que se transformara.
     * @return un Document ( xml ).
     */
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