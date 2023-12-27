package ba.unsa.etf.rpr.chatappserver;

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


}