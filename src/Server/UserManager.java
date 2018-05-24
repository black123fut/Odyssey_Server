package Server;

import DataStructures.*;
import Mensajes.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Scanner;


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

            Scanner scanner = new Scanner(new InputStreamReader(inputStream));

            String xml;
            //Lee cada mensaje nuevo.
            while(scanner.hasNextLine()){
                xml = scanner.nextLine();

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
                else if (mensaje.getOpcode().equalsIgnoreCase("004")){
                    Message<SongMessage> songMessage = mapper.readValue(xml, new TypeReference<Message<SongMessage>>() {});
                    play(songMessage);
                }
                else if (mensaje.getOpcode().equalsIgnoreCase("005")){
                    Message<SongMessage> songMessage = mapper.readValue(xml, new TypeReference<Message<SongMessage>>() {});
                    saveSong(songMessage);
                }
                else if (mensaje.getOpcode().equalsIgnoreCase("007")){
                    Message<SearchMessage> message = mapper.readValue(xml, new TypeReference<Message<SearchMessage>>() {});
                    search(message);
                }
                else if (mensaje.getOpcode().equalsIgnoreCase("009") ||
                            mensaje.getOpcode().equalsIgnoreCase("010") ||
                            mensaje.getOpcode().equalsIgnoreCase("011")) {
                    Message<String[][]> message = mapper.readValue(xml, new TypeReference<Message<String[][]>>() {});
                    Sort(message);
                } else if (mensaje.getOpcode().equalsIgnoreCase("012")){
                    System.out.println(xml);
                    Message<SongMessage> message = mapper.readValue(xml, new TypeReference<Message<SongMessage>>() {});
                    deleteSong(message);
                }

            }

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void deleteSong(Message<SongMessage> message) throws IOException {
        File archivo = new File("songs.json");
        ObjectMapper jsonMapper = new ObjectMapper();
        Song[] tmpSongs = jsonMapper.readValue(archivo, Song[].class);
        LinkedList<Song> songList = new LinkedList<>();

        SongMessage data = message.getData();
        for (int i = 0; i < tmpSongs.length; i++) {
            if (tmpSongs[i].getAlbum().equalsIgnoreCase(data.getAlbum()) &&
                    tmpSongs[i].getTitulo().equalsIgnoreCase(data.getCancion()) &&
                    tmpSongs[i].getArtista().equalsIgnoreCase(data.getArtista())){
                String path = pathMaker(data.getGenero(), data.getArtista(), data.getCancion());
                File archive = new File(path);

                if (archive.delete()){
                    Message<InfoMessage> response = writeInfoMessage("013", "Cancion eliminada exitosamente");

                    XmlMapper xmlMapper = new XmlMapper();
                    send(xmlMapper.writeValueAsString(response));
                }

            } else{
                songList.add(tmpSongs[i]);
            }
        }

        Song[] songs = new Song[songList.length()];
        for (int i = 0; i < songList.length(); i++) {
            songs[i] = songList.get(i);
        }

        jsonMapper.writerWithDefaultPrettyPrinter().writeValue(archivo, songs);
    }

    private void saveSong(Message<SongMessage> message)  throws IOException {
        File archivo = new File("songs.json");
        ObjectMapper jsonMapper = new ObjectMapper();
        Song[] tmpSongs = jsonMapper.readValue(archivo, Song[].class);
        LinkedList<Song> songList = new LinkedList<>();

        for (int i = 0; i < tmpSongs.length; i++) {
            if (tmpSongs[i] != null)
                songList.add(tmpSongs[i]);
        }

        SongMessage data = message.getData();
        for (int i = 0; i < songList.length(); i++) {
            if (songList.get(i) != null){
                if (songList.get(i).getArtista().equalsIgnoreCase(data.getArtista()) &&
                        songList.get(i).getTitulo().equalsIgnoreCase(data.getCancion())){
                    Message<InfoMessage> errorMessage = writeInfoMessage("002", "La Cancion ya existe");

                    XmlMapper xmlMapper = new XmlMapper();
                    send(xmlMapper.writeValueAsString(errorMessage));
                    return;
                }
            }
        }

        String path = pathMaker(data.getGenero(), data.getArtista(), data.getCancion());
        System.out.println(path);
        songList.add(new Song(data.getCancion(), data.getArtista(),
                    data.getGenero(), data.getAlbum(), data.getYear(), data.getDuracion(),
                    data.getLetra(), path));

//        for (int i = 0; i < songList.length(); i++) {
//            System.out.println("Titulo: " + songList.get(i).getTitulo());
//            System.out.println("Year: " + songList.get(i).getYear());
//            System.out.println("Duracion: " + songList.get(i).getDuracion());
//        }

        //Convierte los bytes a un archivo en mp3.
        writeBytesToMp3(Base64.getDecoder().decode(data.getBytes()), path);

        //Convierte la lista en un array
        Song[] songs = new Song[songList.length()];
        for (int i = 0; i < songList.length(); i++) {
            songs[i] = songList.get(i);
        }

        jsonMapper.writerWithDefaultPrettyPrinter().writeValue(archivo, songs);

        Message<InfoMessage> response = writeInfoMessage("006", "Cancion guardada con exito");
        XmlMapper mapper = new XmlMapper();

        send(mapper.writeValueAsString(response));
    }

    private String pathMaker(String genero, String artista, String cancion){
        if(genero == null || genero.equals("")){
            genero = "Otros";
        }
        return "src/Music/" + genero + "/" + artista + " - " + cancion + ".mp3";
    }

    private void registar(Message<SignInMessage> message) throws IOException{
        File archivo = new File("users.json");
        ObjectMapper mapperJson = new ObjectMapper();
        //Lee el archivo json de los usuarios y lo guarda en una variable.
        User[] tmpUsers = mapperJson.readValue(archivo, User[].class);
        LinkedList<User> userList = new LinkedList<>();

        for (int i = 0; i < tmpUsers.length; i++) {
            userList.add(tmpUsers[i]);
        }

        //Verifica que no exista un usuario con el mismo nombre de usuario.
        for (User x: tmpUsers) {
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
        userList.add(new User(message.getData().getUsername(), message.getData().getName()
                , message.getData().getSurname(), message.getData().getAge(), message.getData().getPassword()));
        Message<InfoMessage> response = writeInfoMessage("003", "Aceptado");

        XmlMapper mapper2 = new XmlMapper();
        send(mapper2.writeValueAsString(response));

        User[] users = new User[userList.length()];
        for (int i = 0; i < userList.length(); i++) {
            users[i] = userList.get(i);
        }

        //Sobre-escribe la variable users en un json.
//        XmlMapper fer = new XmlMapper();
//        fer.writerWithDefaultPrettyPrinter().writeValue(new File("test.xml"), users);
        mapperJson.writeValue(archivo, users);
    }

    private void writeBytesToMp3(byte[] bFile, String fileDest) {
        FileOutputStream fileOuputStream = null;

        try {
            fileOuputStream = new FileOutputStream(fileDest);
            fileOuputStream.write(bFile);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOuputStream != null) {
                try {
                    fileOuputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void play(Message<SongMessage> message) throws FileNotFoundException, IOException{
//        File mp3 = new File("src/music/torero.mp3");


//        File tmp = File.createTempFile("test", "mp3");
//        tmp.deleteOnExit();
//        FileOutputStream fos = new FileOutputStream(tmp);
//        fos.write(mp3Array);
//        fos.close();
        File archivo = new File("songs.json");
        ObjectMapper jsonMapper = new ObjectMapper();
        Song[] songs = jsonMapper.readValue(archivo, Song[].class);
        jsonMapper.writeValue(archivo, songs);

        SongMessage data = message.getData();
        byte[] mp3Array;

        for (int i = 0; i < songs.length; i++) {
            if(data.getArtista().equalsIgnoreCase(songs[i].getArtista()) && data.getCancion().equalsIgnoreCase(songs[i].getTitulo())){
                mp3Array = Files.readAllBytes(Paths.get(songs[i].getPath()));

                Message<SongMessage> songMessage = new Message<>();
                songMessage.setOpcode("004");

                //System.out.println(mp3Array[0]);
//              mp3Array = Files.readAllBytes(Paths.get("src/music/torero.mp3"));

                SongMessage song  = new SongMessage();
                song.setBytes(Base64.getEncoder().encodeToString(mp3Array));

                songMessage.setData(song);
                XmlMapper mapper = new XmlMapper();
                send(mapper.writeValueAsString(songMessage));
                return;
            }
        }

        Message<InfoMessage> errorMessage = writeInfoMessage("002", "Error inesperado");

        XmlMapper mapper2 = new XmlMapper();
        //Manda la respuesta al cliente.
        send(mapper2.writeValueAsString(errorMessage));


//        Message<SongMessage> songMessage = new Message<>();
//        songMessage.setOpcode("004");
//
//        //System.out.println(mp3Array[0]);
////        mp3Array = Files.readAllBytes(Paths.get("src/music/torero.mp3"));
//
//        SongMessage song  = new SongMessage();
//        song.setBytes(Base64.getEncoder().encodeToString(mp3Array));
//
//        songMessage.setData(song);
//        XmlMapper mapper = new XmlMapper();
//        send(mapper.writeValueAsString(songMessage));
    }

    public void search(Message<SearchMessage> message) throws  IOException{
        SearchMessage data = message.getData();
        XmlMapper xmlMapper = new XmlMapper();

        File archivo  = new File("songs.json");
        ObjectMapper jsonMapper = new ObjectMapper();
        Song[] songs = jsonMapper.readValue(archivo, Song[].class);

        BinaryTree<Song> tree = new BinaryTree<>();
        AvlTree<Song> avlTree = new AvlTree<>();
        SplayTree<Integer, Song> splayTree = new SplayTree<>();
        BTree<Song> bTree = new BTree<>();

        LinkedList<Song> songList;


        if (data.getType().equalsIgnoreCase("Artista")){
            for (int i = 0; i < songs.length; i++) {
                avlTree.add(songs[i]);
            }
            songList = avlTree.get(message.getData().getInput());
        }
        else if (data.getType().equalsIgnoreCase("Cancion")){
            for (int i = 0; i < songs.length; i++) {
                bTree.insert(songs[i]);
            }
            songList = bTree.get(message.getData().getInput());
        }
        else if (data.getType().equalsIgnoreCase("Album")){
            for (int i = 0; i < songs.length; i++) {
                splayTree.insert(i, songs[i]);
            }
            songList = splayTree.get(message.getData().getInput());
        }
        else {
            Message<InfoMessage> error = writeInfoMessage("002", "Debe ingresar un tipo");
            send(xmlMapper.writeValueAsString(error));
            return;
        }

        Song[] songsArray = new Song[songList.length()];
        for (int i = 0; i < songList.length(); i++) {
            songsArray[i] = songList.get(i);
        }

        Message<Song[]> response = new Message<>();
        response.setOpcode("008");

        response.setData(songsArray);

        send(xmlMapper.writeValueAsString(response));

//        xmlMapper.writeValue(new File("test.xml"), response);
//        System.out.println(xmlMapper.writeValueAsString(response));
//        Message<InfoMessage> response = writeInfoMessage("008", );

        //continuara

    }

    @SuppressWarnings("Duplicates")
    public void Sort(Message<String[][]> message) throws IOException{
        String[][] dataMessage = message.getData();
        LinkedList<Song> songs = new LinkedList<>();
        Sorts sort = new Sorts();
        XmlMapper xmlMapper = new XmlMapper();

        for (int i = 0; i < dataMessage.length; i++) {
            songs.add(new Song());
            songs.get(i).setTitulo(dataMessage[i][0]);
            songs.get(i).setArtista(dataMessage[i][1]);
            songs.get(i).setAlbum(dataMessage[i][2]);
        }

        if (message.getOpcode().equalsIgnoreCase("009"))
            sort.bubblesort(songs);
        else if (message.getOpcode().equalsIgnoreCase("010"))
            sort.radixSort(songs);
        else if (message.getOpcode().equalsIgnoreCase("011"))
            sort.quickSort(songs);

        Song[] songsArray = new Song[songs.length()];
        for (int i = 0; i < songsArray.length; i++) {
            songsArray[i] = songs.get(i);
        }

        Message<Song[]> response = new Message<>();
        response.setOpcode("008");

        response.setData(songsArray);

        send(xmlMapper.writeValueAsString(response));
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

    public byte[] inputStreamToByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int bytesRead;
        while((bytesRead = inputStream.read(buffer)) > 0){
            baos.write(buffer, 0, bytesRead);
        }
        return baos.toByteArray();
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