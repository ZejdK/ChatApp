package ba.unsa.etf.rpr.chatapp;

import ba.unsa.etf.rpr.chatapp.domain.ClientConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;

public class ClientConfigDao {

    private static ClientConfigDao instance = null;

    private ClientConfig clientConfig;

    private ClientConfigDao() {

        try {

            System.out.println("Loading from configuration file...");
            String pathname = "src/main/resources/ba/unsa/etf/rpr/chatapp/configclient.json";
            clientConfig = (new ObjectMapper()).readValue(new File(pathname), ClientConfig.class);
            System.out.println("Successfully loaded config:\n" + clientConfig.getServerUrl() + "\n" + clientConfig.getServerPort());

        } catch (Exception e) {

            e.printStackTrace();
            throw new RuntimeException("Error loading from the configuration file");
        }
    }

    public static ClientConfigDao getInstance() {

        if (instance == null)
            instance = new ClientConfigDao();

        return instance;
    }

    public static String getServerUrl() {
        return instance.clientConfig.getServerUrl();
    }

    public static int getServerPort() {
        return instance.clientConfig.getServerPort();
    }
}
