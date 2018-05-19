package Server;

public class Song {
    private String titulo;
    private String artista;
    private String genero;
    private String album;
    private String letra;
    private String path;

    public Song(){

    }

    public Song(String titulo, String artista, String genero, String album, String letra, String path) {
        this.titulo = titulo;
        this.artista = artista;
        this.genero = genero;
        this.album = album;
        this.letra = letra;
        this.path = path;
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

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
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
}
