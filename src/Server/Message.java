package Server;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "Message")
public class Message {
    private String opcode;
    @JacksonXmlProperty(localName = "Data")
    @JacksonXmlElementWrapper(useWrapping = false)
    private SignInMessage data;

    public Message(){

    }

    public String getOpcode() {
        return opcode;
    }

    public void setOpcode(String opcode) {
        this.opcode = opcode;
    }

    public SignInMessage getData() {
        return data;
    }

    public void setData(SignInMessage data) {
        this.data = data;
    }
}
