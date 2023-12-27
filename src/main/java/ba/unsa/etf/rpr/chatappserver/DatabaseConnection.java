package ba.unsa.etf.rpr.chatappserver;

import at.favre.lib.crypto.bcrypt.BCrypt;
import ba.unsa.etf.rpr.LoginData;
import ba.unsa.etf.rpr.User;

import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class DatabaseConnection {

    private final Connection databaseConnection;

    private static DatabaseConnection instance = null;

    private DatabaseConnection() {

        try {
            System.out.println("Connecting to the database...");

            Properties p = new Properties();
            p.load(ClassLoader.getSystemResource("server.properties").openStream());

            String connectionUrl = String.format("jdbc:mysql://%s:%d/%s", p.getProperty("db.ip"), Integer.parseInt(p.getProperty("db.port")), p.getProperty("db.name"));
            databaseConnection = DriverManager.getConnection(connectionUrl, p.getProperty("db.user"), p.getProperty("db.password"));

            System.out.println("Successfully connected to the database!");

        } catch (Exception e) { e.printStackTrace(); throw new RuntimeException("Failed to connect to the database!"); }
    }

    public static DatabaseConnection getInstance() {

        if (instance == null) {

            try {
                instance = new DatabaseConnection();
            } catch (Exception e) { e.printStackTrace(); }
        }
        return instance;
    }

    public ResultSet runQuery(String query) throws SQLException {

        PreparedStatement ps = databaseConnection.prepareStatement(query);
        return ps.executeQuery();
    }

    public User Lookup(String username) throws SQLException {

        int rowCount = 0;
        User user = null;
        ResultSet rs = this.runQuery(String.format("SELECT * FROM users WHERE username = '%s'", username));

        while (rs.next()) {

            user = new User(rs.getLong("id"), rs.getString("username"), rs.getString("passwordhash"));
            System.out.printf("id %d username %s password %s\n", user.getDatabaseId(), user.getUsername(), user.getPasswordHash());
            ++rowCount;
        }

        if (rowCount > 1)
            System.out.println("WARNING: Multiple users with the same username returned from the database!");
        System.out.println("Read " + rowCount + " rows from the database");

        return user;
    }

    public User CreateUser(LoginData loginData) {

        try {
            PreparedStatement userInsertQuery = databaseConnection.prepareStatement("INSERT INTO users (username, passwordhash) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
            userInsertQuery.setString(1, loginData.username);
            userInsertQuery.setString(2, BCrypt.withDefaults().hashToString(12, loginData.password.toCharArray()));

            userInsertQuery.executeUpdate();

            ResultSet keys = userInsertQuery.getGeneratedKeys();

            System.out.println("Fetch size: " + keys.getFetchSize());

            while (keys.next()) {
                System.out.println(keys);
            }

            return Lookup(loginData.username);

        } catch (Exception e) { e.printStackTrace(); System.out.println("Error creating user!"); }

        return null;
    }
}