package Mensajes;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class MetadataMessage extends Message {
    @JacksonXmlProperty(localName="type")
    private String type;
    @JacksonXmlProperty(localName="cancion")
    private String cancion;
    @JacksonXmlProperty(localName="artista")
    private String artista;
    @JacksonXmlProperty(localName="text")
    private String info;

    public MetadataMessage(){
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCancion() {
        return cancion;
    }

    public void setCancion(String cancion) {
        this.cancion = cancion;
    }

    public String getArtista() {
        return artista;
    }

    public void setArtista(String artista) {
        this.artista = artista;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
