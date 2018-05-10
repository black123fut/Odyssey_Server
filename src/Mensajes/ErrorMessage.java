package Mensajes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorMessage {
    @JacksonXmlProperty(localName="error")
    private String error;

    public ErrorMessage(){

    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
