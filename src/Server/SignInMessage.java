package Server;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class SignInMessage extends Message {
    @JacksonXmlProperty(localName="username")
    private String username;
    @JacksonXmlProperty(localName="name")
    private String name;
    @JacksonXmlProperty(localName="surname")
    private String surname;
    @JacksonXmlProperty(localName="age")
    private int age;

    public SignInMessage(){

    }

    public SignInMessage(String username, String name, String surname, int age) {
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.age = age;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
