package ba.unsa.etf.rpr.chatapp;

import ba.unsa.etf.rpr.chatapp.business.ServerConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Properties;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class ChatAppClient extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        Properties p = new Properties();
        p.load(ClassLoader.getSystemResource("client.properties").openStream());

        ServerConnection s = new ServerConnection(p.getProperty("server.ip"), Integer.parseInt(p.getProperty("server.port")));

        FXMLLoader loader = new FXMLLoader(ChatAppClient.class.getResource("loginWindow.fxml"));
        LoginWindowController loginWindowController = new LoginWindowController(s);
        loader.setController(loginWindowController);

        s.startListener();

        stage.setTitle("Log in");
        stage.setScene(new Scene(loader.load(), USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}