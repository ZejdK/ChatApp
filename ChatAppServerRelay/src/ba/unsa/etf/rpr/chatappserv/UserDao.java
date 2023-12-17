package ba.unsa.etf.rpr.chatappserv;

public class UserDao {

    private long databaseId;
    private String username;
    private String passwordHash;

    public UserDao(long databaseId, String username, String passwordHash) {
        this.databaseId = databaseId;
        this.username = username;
        this.passwordHash = passwordHash;
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
}
