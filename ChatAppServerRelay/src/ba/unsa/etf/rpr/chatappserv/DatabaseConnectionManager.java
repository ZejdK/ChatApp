package ba.unsa.etf.rpr.chatappserv;

import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class DatabaseConnectionManager {

    private final Connection databaseConnection;

    public DatabaseConnectionManager() throws SQLException, IOException {

        Properties p = new Properties();
        p.load(ClassLoader.getSystemResource("server.properties").openStream());

        String connectionUrl = String.format("jdbc:mysql://%s:%d/%s", p.getProperty("db.ip"), Integer.parseInt(p.getProperty("db.port")), p.getProperty("db.name"));
        databaseConnection = DriverManager.getConnection(connectionUrl, p.getProperty("db.user"), p.getProperty("db.password"));
    }

    public ResultSet runQuery(String query) throws SQLException {

        PreparedStatement ps = databaseConnection.prepareStatement(query);
        return ps.executeQuery();
    }

    public UserDao LookupUser(String username) throws SQLException {

        int rowCount = 0;
        UserDao user = null;
        ResultSet rs = this.runQuery(String.format("SELECT * FROM user WHERE username = '%s'", username));

        while (rs.next()) {

            user = new UserDao(rs.getLong("id"), rs.getString("username"), rs.getString("passwordhash"));
            System.out.printf("id %d username %s password %s\n", user.getDatabaseId(), user.getUsername(), user.getPasswordHash());
            ++rowCount;
        }

        if (rowCount > 1)
            System.out.println("WARNING: Multiple users with the same username have popped up!");
        System.out.println("Read " + rowCount + " rows from the database");

        return user;
    }
}
