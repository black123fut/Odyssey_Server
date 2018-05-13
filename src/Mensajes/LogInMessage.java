package Mensajes;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.apache.commons.codec.digest.DigestUtils;

public class LogInMessage extends Message {
    @JacksonXmlProperty(localName="username")
    private String username;
    @JacksonXmlProperty(localName="password")
    private String password;

    public LogInMessage(){

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = DigestUtils.md5Hex(password).toUpperCase();
    }
}
