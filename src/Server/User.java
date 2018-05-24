package Server;

public class User implements Comparable<User>{
    private String username;
    private String name;
    private String surname;
    private int age;
    private String password;

    public User(){
    }

    public User(String username, String name, String surname, int age, String password) {
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public int compareTo(User o) {
        if (getUsername().compareTo(o.getUsername()) < 0){
            return -1;
        } else if (getUsername().compareTo(o.getUsername()) > 0){
            return 1;
        } else{
            return 0;
        }
    }
}
















