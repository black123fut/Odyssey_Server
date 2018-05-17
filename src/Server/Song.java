package Server;

public class Song {
    private String titulo;
    private String artista;
    private String album;
    private String letra;
    private String path;
    private String bytes;

    public Song(){

    }

    public Song(String titulo, String artista, String album, String letra, String path, String bytes) {
        this.titulo = titulo;
        this.artista = artista;
        this.album = album;
        this.letra = letra;
        this.path = path;
        this.bytes = bytes;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getArtista() {
        return artista;
    }

    public void setArtista(String artista) {
        this.artista = artista;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getLetra() {
        return letra;
    }

    public void setLetra(String letra) {
        this.letra = letra;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getBytes() {
        return bytes;
    }

    public void setBytes(String bytes) {
        this.bytes = bytes;
    }
}
