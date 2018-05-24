package Mensajes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SongMessage extends Message {
    @JacksonXmlProperty(localName="cancion")
    private String cancion;
    @JacksonXmlProperty(localName="artista")
    private String artista;
    @JacksonXmlProperty(localName="album")
    private String album;
    @JacksonXmlProperty(localName="genero")
    private String genero;
    @JacksonXmlProperty(localName="year")
    private String year;
    @JacksonXmlProperty(localName="duracion")
    private String duracion;
    @JacksonXmlProperty(localName="letra")
    private String letra;
    @JacksonXmlProperty(localName="bytes")
    private String bytes;

    public SongMessage(){

    }

    public String getCancion() {
        return cancion;
    }

    public void setCancion(String titulo) {
        this.cancion = titulo;
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

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
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

    public String getBytes() {
        return bytes;
    }

    public void setBytes(String bytes) {
        this.bytes = bytes;
    }
}
