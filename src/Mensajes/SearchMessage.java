package Mensajes;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class SearchMessage extends Message {
    @JacksonXmlProperty(localName="type")
    private String type;
    @JacksonXmlProperty(localName="text")
    private String input;

    public SearchMessage() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }
}
