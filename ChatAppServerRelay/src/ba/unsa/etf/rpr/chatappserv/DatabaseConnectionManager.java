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
}
