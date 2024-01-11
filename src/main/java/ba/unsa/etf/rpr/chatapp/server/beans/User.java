package ba.unsa.etf.rpr.chatapp.server.beans;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {

    private long id;
    private String username;
    private String password;
    private ArrayList<Long> roles;

    public User(long id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.roles = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
        this.password = password;
    }

    public String getInfo() {
        return "[" + id + "] " + username;
    }

    public ArrayList<Long> getRoles() {
        return roles;
    }

    public void setRoles(ArrayList<Long> roles) {
        this.roles = roles;
    }
}

