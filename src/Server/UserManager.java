package Server;

import DataStructures.*;
import Mensajes.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

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

    /**
     * Constructor.
     * @param server Instancia del Server Socket.
     * @param clientSocket Socket que esta usando este cliente.
     */
    public UserManager(Server server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
    }

    /**
     * Metodo que inicia la actividad del cliente.
     */
    @Override
    public void run(){
        try{
            outputStream = new PrintWriter(clientSocket.getOutputStream(), true);
            inputStream = clientSocket.getInputStream();

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
                    //Convierte el xml a un Mensaje con Data de clase SignInMessage.
                    Message<SignInMessage> messageInformation = mapper.readValue(xml, new TypeReference<Message<SignInMessage>>() {});
                    registar(messageInformation);
                }
                //Logea a un usuario.
                else if (mensaje.getOpcode().equalsIgnoreCase("001")){
                    //Convierte el xml a un Mensaje con Data de clase LogInMessage.
                    Message<LogInMessage> messageInformation = mapper.readValue(xml, new TypeReference<Message<LogInMessage>>() {});
                    iniciarSesion(messageInformation);
                }
                //Va al metodo para reproducir musica.
                else if (mensaje.getOpcode().equalsIgnoreCase("004")){
                    //Convierte el xml a un Mensaje con Data de clase SongMessage.
                    Message<SongMessage> songMessage = mapper.readValue(xml, new TypeReference<Message<SongMessage>>() {});
                    play(songMessage);
                }
                //Se dirigue al metodo para guardar una cancion.
                else if (mensaje.getOpcode().equalsIgnoreCase("005")){
                    //Convierte el xml a un Mensaje con Data de clase SongMessage.
                    Message<SongMessage> songMessage = mapper.readValue(xml, new TypeReference<Message<SongMessage>>() {});
                    saveSong(songMessage);
                }
                //Se dirigue al metodo de buscar.
                else if (mensaje.getOpcode().equalsIgnoreCase("007")){
                    //Convierte el xml a un Mensaje con Data de clase SearchMessage.
                    Message<SearchMessage> message = mapper.readValue(xml, new TypeReference<Message<SearchMessage>>() {});
                    search(message);
                }
                //Abre el metodo para ordenar.
                else if (mensaje.getOpcode().equalsIgnoreCase("009") ||
                            mensaje.getOpcode().equalsIgnoreCase("010") ||
                            mensaje.getOpcode().equalsIgnoreCase("011")) {
                    //Convierte el xml a un Mensaje con Data en un String[][].
                    Message<String[][]> message = mapper.readValue(xml, new TypeReference<Message<String[][]>>() {});
                    Sort(message);
                }
                //Abre el metodo que elimina canciones.
                else if (mensaje.getOpcode().equalsIgnoreCase("012")){
                    //Convierte el xml a un Mensaje con Data de clase SongMessage.
                    Message<SongMessage> message = mapper.readValue(xml, new TypeReference<Message<SongMessage>>() {});
                    deleteSong(message);
                }
                //Abre el metodo para modificar la metadata de una cancion.
                else if (mensaje.getOpcode().equalsIgnoreCase("014")){
                    //Convierte el xml a un Mensaje con Data de clase MetadataMessage.
                    Message<MetadataMessage> message = mapper.readValue(xml, new TypeReference<Message<MetadataMessage>>() {});
                    setMetadata(message);
                }
                //Abre el metodo para buscar canciones por la letra.
                else if (mensaje.getOpcode().equalsIgnoreCase("015")){
                    Message<SearchMessage> message = mapper.readValue(xml, new TypeReference<Message<SearchMessage>>() {});
                    searchLyrics(message);
                }
            }
            clientSocket.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Metodo que busca las canciones con la letra que se ingreso.
     * @param message Objeto que contiene la informacion.
     */
    private void searchLyrics(Message<SearchMessage> message) throws IOException{
        File archivo = new File("songs.json");
        ObjectMapper jsonMapper = new ObjectMapper();
        Song[] tmpSong = jsonMapper.readValue(archivo, Song[].class);
        LinkedList<Song> list = new LinkedList<>();
        LinkedList<Song> results = new LinkedList<>();

        //Agrega las canciones a una lista.
        for (int i = 0; i < tmpSong.length; i++) {
            list.add(tmpSong[i]);
        }

        String input = message.getData().getInput();

        //Compara la letra con el input del cliente
        for (int i = 0; i < list.length(); i++) {
            Scanner scan = new Scanner(input).useDelimiter("\\s* \\s*");
            Scanner lyricScan = new Scanner(list.get(i).getLetra()).useDelimiter("\\s* \\s*");

            while (scan.hasNext()){
                String text = scan.next();
                while(lyricScan.hasNext()){
                    String word = lyricScan.next();
                    if (text.equalsIgnoreCase(word)){
                        results.add(list.get(i));
                        break;
                    }
                }
                if (lyricScan.hasNext()){
                    break;
                }
            }
        }

        Song[] songsArray = new Song[results.length()];
        //Pasa la lista a un array.
        for (int i = 0; i < results.length(); i++) {
            songsArray[i] = results.get(i);
        }

        XmlMapper xmlMapper  = new XmlMapper();
        //Mensaje de respuesta al cliente.
        Message<Song[]> response = new Message<>();
        response.setOpcode("008");
        response.setData(songsArray);
        send(xmlMapper.writeValueAsString(response));
    }

    /**
     * Metodo que busca las canciones con la informacion que se pidio buscar.
     * @param message Objeto que contiene la informacion.
     */
    public void search(Message<SearchMessage> message) throws  IOException{
        SearchMessage data = message.getData();
        XmlMapper xmlMapper = new XmlMapper();

        //Abre el archivo json.
        File archivo  = new File("songs.json");
        ObjectMapper jsonMapper = new ObjectMapper();
        //Passa el json a un array.
        Song[] songs = jsonMapper.readValue(archivo, Song[].class);

        LinkedList<Song> songList;

        //Verifica el tipo de busqueda que tiene que hacer.
        if (data.getType().equalsIgnoreCase("Artista")){
            AvlTree<Song> avlTree = new AvlTree<>();
            //Agrega los elementos de la lista al arbol AVL.
            for (int i = 0; i < songs.length; i++) {
                avlTree.add(songs[i]);
            }
            //Guarda en la lista los elementos del arbol que sean igual al artista buscado.
            songList = avlTree.get(message.getData().getInput());
        }
        else if (data.getType().equalsIgnoreCase("Cancion")){
            BTree<Song> bTree = new BTree<>();
            //Agrega los elementos de la lista al arbol B.
            for (int i = 0; i < songs.length; i++) {
                bTree.insert(songs[i]);
            }
            //Guarda en la lista los elementos del arbol que sean igual al nombre de la cancion buscada.
            songList = bTree.get(message.getData().getInput());
        }
        else if (data.getType().equalsIgnoreCase("Album")){
            SplayTree<Integer, Song> splayTree = new SplayTree<>();
            //Agrega los elementos de la lista al arbol Splay.
            for (int i = 0; i < songs.length; i++) {
                splayTree.insert(i, songs[i]);
            }
            //Guarda en la lista los elementos del arbol que sean igual al album buscado.
            songList = splayTree.get(message.getData().getInput());
        }
        else {
            Message<InfoMessage> error = writeInfoMessage("002", "Debe ingresar un tipo");
            send(xmlMapper.writeValueAsString(error));
            return;
        }

        Song[] songsArray = new Song[songList.length()];
        //Pasa la lista a un array.
        for (int i = 0; i < songList.length(); i++) {
            songsArray[i] = songList.get(i);
        }

        Message<Song[]> response = new Message<>();
        response.setOpcode("008");

        response.setData(songsArray);

        send(xmlMapper.writeValueAsString(response));
    }

    /**
     * Metodo que modifica la informacion de la cancion en el archivo Json.
     * @param message Objeto que contiene la informacion.
     */
    private void setMetadata(Message<MetadataMessage> message) throws IOException{
        //Abre el archivo json.
        File archivo = new File("songs.json");
        ObjectMapper jsonMapper = new ObjectMapper();
        //Convierte el json en un array.
        Song[] tmpSong= jsonMapper.readValue(archivo, Song[].class);
        LinkedList<Song> sonList = new LinkedList<>();
        XmlMapper xmlMapper = new XmlMapper();

        MetadataMessage data = message.getData();

        //Pasa el array a una lista.
        for (int i = 0; i < tmpSong.length; i++) {
            sonList.add(tmpSong[i]);
        }

        //Busca la cancion a la que hay que modificar la informacion.
        for (int i = 0; i < sonList.length(); i++) {
            if (sonList.get(i).getTitulo().equalsIgnoreCase(data.getCancion()) &&
                    sonList.get(i).getArtista().equalsIgnoreCase(data.getArtista())){
                if (data.getType().equalsIgnoreCase("Artista")){
                    sonList.get(i).setArtista(data.getInfo());
                } else if (data.getType().equalsIgnoreCase("Cancion")){
                    sonList.get(i).setTitulo(data.getInfo());
                } else if (data.getType().equalsIgnoreCase("Album")){
                    sonList.get(i).setAlbum(data.getInfo());
                } else if (data.getType().equalsIgnoreCase("Año") || data.getType().equalsIgnoreCase("year")){
                    sonList.get(i).setYear(data.getInfo());
                } else if (data.getType().equalsIgnoreCase("Letra")){
                    sonList.get(i).setLetra(data.getInfo());
                } else {
                    Message<InfoMessage> response = writeInfoMessage("002", "Seleccione un tipo");
                    send(xmlMapper.writeValueAsString(response));
                }
                break;
            }
        }

        Song[] songs = new Song[sonList.length()];
        //Pasa la lista a un array.
        for (int i = 0; i < sonList.length(); i++) {
            songs[i] = sonList.get(i);
        }

        Message<InfoMessage> response = writeInfoMessage("014", "Exito alcambiar los datos");
        //Envia un mensaje de respuesta al cliente.
        send(xmlMapper.writeValueAsString(response));

        jsonMapper.writerWithDefaultPrettyPrinter().writeValue(archivo, songs);
    }

    /**
     * Metodo que elimina una cancion.
     * @param message  Objeto que contiene la informacion.
     */
    private void deleteSong(Message<SongMessage> message) throws IOException {
        //Abre el archivo Json.
        File archivo = new File("songs.json");
        ObjectMapper jsonMapper = new ObjectMapper();
        //Pasa el archivo json a una array.
        Song[] tmpSongs = jsonMapper.readValue(archivo, Song[].class);
        LinkedList<Song> songList = new LinkedList<>();

        SongMessage data = message.getData();
        //Busca la cancion que se va a eliminar.
        for (int i = 0; i < tmpSongs.length; i++) {
            if (tmpSongs[i].getAlbum().equalsIgnoreCase(data.getAlbum()) &&
                    tmpSongs[i].getTitulo().equalsIgnoreCase(data.getCancion()) &&
                    tmpSongs[i].getArtista().equalsIgnoreCase(data.getArtista())){
                String path = pathMaker(tmpSongs[i].getGenero(), data.getArtista(), data.getCancion());
                File archive = new File(path);

                //Si encontro la cancion de la ruta retorna true y la elimina.
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
        //Pasa la lista a un array.
        for (int i = 0; i < songList.length(); i++) {
            songs[i] = songList.get(i);
        }

        jsonMapper.writerWithDefaultPrettyPrinter().writeValue(archivo, songs);
    }

    /**
     * Metodo que guarda la cancion.
     * @param message Objeto que contiene la informacion.
     */
    private void saveSong(Message<SongMessage> message)  throws IOException {
        //Abre el archivo json.
        File archivo = new File("songs.json");
        ObjectMapper jsonMapper = new ObjectMapper();
        //Pasa el json a un array.
        Song[] tmpSongs = jsonMapper.readValue(archivo, Song[].class);
        LinkedList<Song> songList = new LinkedList<>();

        //Pasa el array a una lista.
        for (int i = 0; i < tmpSongs.length; i++) {
            if (tmpSongs[i] != null)
                songList.add(tmpSongs[i]);
        }

        SongMessage data = message.getData();
        //Verifica que la cancion no exista.
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
        //Crea una ruta para guardar la cancion.
        String path = pathMaker(data.getGenero(), data.getArtista(), data.getCancion());
        String carpetPath = pathMaker(data.getGenero());

        //Agrega la cancion a la lista.
        songList.add(new Song(data.getCancion(), data.getArtista(),
                    data.getGenero(), data.getAlbum(), data.getYear(), data.getDuracion(),
                    data.getLetra(), path));

        //Convierte los bytes a un archivo en mp3.
        writeBytesToMp3(Base64.getDecoder().decode(data.getBytes()), path, carpetPath);

        Song[] songs = new Song[songList.length()];
        //Convierte la lista en un array
        for (int i = 0; i < songList.length(); i++) {
            songs[i] = songList.get(i);
        }

        jsonMapper.writerWithDefaultPrettyPrinter().writeValue(archivo, songs);

        Message<InfoMessage> response = writeInfoMessage("006", "Cancion guardada con exito");

        XmlMapper mapper = new XmlMapper();
        send(mapper.writeValueAsString(response));
    }

    /**
     * Metodo que escribe los bytes en un mp3.
     * @param bFile Bytes del mp3.
     * @param mp3Path Direccion en la que se va a guardar la cancion.
     */
    private void writeBytesToMp3(byte[] bFile, String mp3Path, String carpetPath) {
        FileOutputStream fileOuputStream = null;

        try {
            fileOuputStream = new FileOutputStream(mp3Path);
            //Crea el archivo mp3.
            fileOuputStream.write(bFile);
        } catch (IOException e) {
            File carpet = new File(carpetPath);

            if (carpet.mkdir()){
                try {
                    fileOuputStream = new FileOutputStream(mp3Path);
                    fileOuputStream.write(bFile);
                }catch (IOException e1){
                    e1.printStackTrace();
                }
            }
        }
        finally {
            if (fileOuputStream != null) {
                try {
                    fileOuputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * Metodo que crea una ruta para guardar la cancion.
     * @param genero Genero de la cancion.
     * @param artista Artista de la cancion.
     * @param cancion Nombre de la cancion.
     * @return una ruta para guardar la cancion.
     */
    private String pathMaker(String genero, String artista, String cancion){
        if(genero == null || genero.equals("")){
            genero = "Otros";
        }
        return "src/Music/" + genero + "/" + artista + " - " + cancion + ".mp3";
    }

    /**
     * Metodo que crea una ruta para guardar la cancion.
     * @param genero Genero de la cancion.
     * @return una ruta para guardar la cancion.
     */
    private String pathMaker(String genero){
        if(genero == null || genero.equals("")){
            genero = "Otros";
        }
        return "src/Music/" + genero;
    }

    /**
     * Metodo que agrega un usuario mas al archivo json.
     * @param message Objeto que contiene la informacion.
     */
    private void registar(Message<SignInMessage> message) throws IOException{
        //Abre el archivo json.
        File archivo = new File("users.json");
        ObjectMapper mapperJson = new ObjectMapper();
        //Lee el archivo json de los usuarios y lo guarda en una variable.
        User[] tmpUsers = mapperJson.readValue(archivo, User[].class);
        LinkedList<User> userList = new LinkedList<>();

        //Pasa el array a una lista.
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
        //Pasa la lista a un array.
        for (int i = 0; i < userList.length(); i++) {
            users[i] = userList.get(i);
        }

        mapperJson.writerWithDefaultPrettyPrinter().writeValue(archivo, users);
        server.updateUserTree();
    }

    /**
     * Metodo que busca la cancion que se va a reproducir.
     * @param message Objeto que contiene la informacion.
     */
    public void play(Message<SongMessage> message) throws FileNotFoundException, IOException{
        //Abre el archivo json.
        File archivo = new File("songs.json");
        ObjectMapper jsonMapper = new ObjectMapper();
        //Pasa el json a un array.
        Song[] songs = jsonMapper.readValue(archivo, Song[].class);
        jsonMapper.writeValue(archivo, songs);

        SongMessage data = message.getData();
        byte[] mp3Array;

        //Busca la cancion que se va a reproducir.
        for (int i = 0; i < songs.length; i++) {
            if(data.getArtista().equalsIgnoreCase(songs[i].getArtista()) &&
                    data.getCancion().equalsIgnoreCase(songs[i].getTitulo())){
                //Extrae los bytes del mp3.
                mp3Array = Files.readAllBytes(Paths.get(songs[i].getPath()));

                Message<SongMessage> songMessage = new Message<>();
                songMessage.setOpcode("004");

                SongMessage song  = new SongMessage();
                song.setLetra(songs[i].getLetra());
                song.setBytes(Base64.getEncoder().encodeToString(mp3Array));

                songMessage.setData(song);
                XmlMapper mapper = new XmlMapper();
                //Envia el mensaje con los bytes codificados al cliente.
                send(mapper.writeValueAsString(songMessage));
                return;
            }
        }

        Message<InfoMessage> errorMessage = writeInfoMessage("002", "Error inesperado");

        XmlMapper mapper2 = new XmlMapper();
        //Manda la respuesta al cliente.
        send(mapper2.writeValueAsString(errorMessage));
    }

    /**
     * Metodo que ordena una string[][] enviada por el cliente.
     * @param message Objeto que contiene la informacion.
     */
    private void Sort(Message<String[][]> message) throws IOException{
        String[][] dataMessage = message.getData();
        LinkedList<Song> songs = new LinkedList<>();
        Sorts sort = new Sorts();
        XmlMapper xmlMapper = new XmlMapper();

        //Pasa los datos del mensaje a la lista de Song.
        for (int i = 0; i < dataMessage.length; i++) {
            songs.add(new Song());
            songs.get(i).setTitulo(dataMessage[i][0]);
            songs.get(i).setArtista(dataMessage[i][1]);
            songs.get(i).setAlbum(dataMessage[i][2]);
            songs.get(i).setYear(dataMessage[i][3]);
            songs.get(i).setDuracion(dataMessage[i][4]);
        }

        //Verifica que tipo de dato debe ordenar.
        if (message.getOpcode().equalsIgnoreCase("009"))
            sort.bubblesort(songs);
        else if (message.getOpcode().equalsIgnoreCase("010"))
            sort.radixSort(songs);
        else if (message.getOpcode().equalsIgnoreCase("011"))
            sort.quickSort(songs);

        Song[] songsArray = new Song[songs.length()];
        //Pasa una lista a un array.
        for (int i = 0; i < songsArray.length; i++) {
            songsArray[i] = songs.get(i);
        }

        Message<Song[]> response = new Message<>();
        response.setOpcode("008");

        response.setData(songsArray);

        send(xmlMapper.writeValueAsString(response));
    }

    /**
     * Metodo que verifica que el usurario exista en el json.
     * @param message Objeto que contiene la informacion.
     */
    private void iniciarSesion(Message<LogInMessage> message) throws IOException{
        //Abre el archivo json.
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
}