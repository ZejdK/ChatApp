package ba.unsa.etf.rpr.chatappserv;

import java.sql.*;

public class DatabaseConnectionManager {

    private final Connection databaseConnection;
    // private ServerConfigDao serverConfigDao;

    public DatabaseConnectionManager(ServerConfigDao s) throws SQLException {

        String connectionUrl = String.format("jdbc:mysql://%s:%d/%s", s.getDatabaseUrl(), s.getDatabasePort(), s.getDatabaseName());
        databaseConnection = DriverManager.getConnection(connectionUrl, s.getDatabaseUser(), s.getDatabasePassword());
        // s = serverConfigDao;
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
