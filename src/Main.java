import Server.*;

public class Main {

    /**
     * Constructor.
     * @param args argumentos del main.
     */
    public static void main(String[] args) {
        int port = 8000;

        Server server = new Server(port);
        server.start();
    }
}
