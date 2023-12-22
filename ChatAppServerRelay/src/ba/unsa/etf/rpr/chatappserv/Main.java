package ba.unsa.etf.rpr.chatappserv;

import java.util.Properties;

public class Main {

    public static void main(String[] args) {

        System.out.println("Logging onto the database...");
        DatabaseConnectionManager dbm = ConnectToDatabase();
        System.out.println("Successfully connected to the database!");

        System.out.println("Starting connection manager...");
        try {

            Properties p = new Properties();
            p.load(ClassLoader.getSystemResource("server.properties").openStream());

            ConnectionManager cm = new ConnectionManager(dbm);
            cm.startServer(Integer.parseInt(p.getProperty("server.port")));

        } catch (Exception e) { e.printStackTrace(); }
    }

    private static DatabaseConnectionManager ConnectToDatabase() throws RuntimeException {

        try {
            return new DatabaseConnectionManager();
        } catch (Exception e) {
            e.printStackTrace();
            String errorMsg = "Unable to start the server: There was an error connecting to the database!";
            System.out.println(errorMsg);
            System.exit(0);
            throw new RuntimeException(errorMsg);
        }
    }
}
