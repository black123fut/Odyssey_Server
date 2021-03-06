package Server;

public class Song implements Comparable<Song>{
    private String titulo;
    private String artista;
    private String genero;
    private String album;
    private String year;
    private String duracion;
    private String letra;
    private String path;

    public Song(){

    }

    /**
     * Constructor.
     * @param titulo Titulo de la cancion.
     * @param artista Artista de la cancion.
     * @param genero Genero de la cancion.
     * @param album Album de la cancion.
     * @param year Year de la cancion.
     * @param duracion Duracion de la cancion.
     * @param letra Letra de la cancion.
     * @param path Ruta de la cancion.
     */
    public Song(String titulo, String artista, String genero, String album, String year, String duracion, String letra, String path) {
        this.titulo = titulo;
        this.artista = artista;
        this.genero = genero;
        this.album = album;
        this.year = year;
        this.duracion = duracion;
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

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getDuracion() {
        return duracion;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
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

    /**
     * Metodo que compara el nombre de los album.
     * @param o La cancion con la que se compara.
     * @return El valor de ubicacion.
     */
    public int compareAlbum(Song o){
        if (getAlbum().compareTo(o.getAlbum()) < 0){
            return -1;
        } else if (getAlbum().compareTo(o.getAlbum()) > 0){
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Metodo que compara el nombre de las canciones.
     * @param o La cancion con la que se compara.
     * @return El valor de ubicacion.
     */
    @Override
    public int compareTo(Song o) {
        if (getTitulo().compareTo(o.getTitulo()) < 0){
            return -1;
        } else if (getTitulo().compareTo(o.getTitulo()) > 0){
            return 1;
        } else {
            return 0;
        }    }
}