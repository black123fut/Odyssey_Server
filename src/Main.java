import Server.*;
import javafx.scene.media.AudioClip;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        int port = 8000;

        Server server = new Server(port);
        server.start();
    }
}
