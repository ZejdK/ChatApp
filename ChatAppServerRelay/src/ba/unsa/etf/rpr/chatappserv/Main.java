package ba.unsa.etf.rpr.chatappserv;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;

public class Main {

    public static void main(String[] args) {

        System.out.println("Logging onto the database...");
        ServerConfigDao servConfig = LoadServerConfiguration();
        DatabaseConnectionManager dbm = ConnectToDatabase(servConfig);
        System.out.println("Successfully connected to the database!");

        System.out.println("Starting connection manager...");
        try {
            ConnectionManager cm = new ConnectionManager(dbm);
            cm.startServer(servConfig.getServerPort());
        } catch (Exception e) { e.printStackTrace(); }
    }

    private static ServerConfigDao LoadServerConfiguration() throws RuntimeException {

        try {
            String filepath = "C:\\Users\\Zejd\\IdeaProjects\\RPR\\Projekt\\ChatApp\\ChatAppServerRelay\\resources\\configserv.json";
            return (new ObjectMapper()).readValue(new File(filepath), ServerConfigDao.class);
        } catch (Exception e) {
            e.printStackTrace();
            String errorMsg = "Unable to start the server: There was an error loading the configuration file!";
            System.out.println(errorMsg);
            System.exit(0);
            throw new RuntimeException(errorMsg);
        }
    }

    private static DatabaseConnectionManager ConnectToDatabase(ServerConfigDao servConfig) throws RuntimeException {

        try {
            return new DatabaseConnectionManager(servConfig);
        } catch (Exception e) {
            e.printStackTrace();
            String errorMsg = "Unable to start the server: There was an error connecting to the database!";
            System.out.println(errorMsg);
            System.exit(0);
            throw new RuntimeException(errorMsg);
        }
    }
}
