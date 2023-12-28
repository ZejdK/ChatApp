package ba.unsa.etf.rpr;

import java.util.ArrayList;

public class User {

    private long databaseId;
    private String username;
    private String passwordHash;
    private ArrayList<String> roles;

    public User(long databaseId, String username, String passwordHash) {
        this.databaseId = databaseId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.roles = new ArrayList<>();
    }

    public long getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(long databaseId) {
        this.databaseId = databaseId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getInfo() {
        return "[" + databaseId + "] " + username;
    }

    public ArrayList<String> getRoles() {
        return roles;
    }

    public void setRoles(ArrayList<String> roles) {
        this.roles = roles;
    }
}

