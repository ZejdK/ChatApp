package ba.unsa.etf.rpr.chatapp.client.model;

import ba.unsa.etf.rpr.chatapp.shared.dto.LoginData;
import ba.unsa.etf.rpr.chatapp.shared.dto.ServerResponseCode;
import ba.unsa.etf.rpr.chatapp.client.business.ServerConnection;
import ba.unsa.etf.rpr.chatapp.client.controller.MainWindowController;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class LoginModel {

    private final ServerConnection serverConn;
    private final SimpleStringProperty infoMessage;

    public LoginModel(ServerConnection serverConn) {

        this.serverConn = serverConn;
        infoMessage = new SimpleStringProperty("");


        serverConn.addConsumer((Object o) -> {

            System.out.println("received object from the server " + o);

            if (o instanceof ServerResponseCode srcode) {

                System.out.println("received server response code " + srcode);

                if (srcode.equals(ServerResponseCode.LOGIN_SUCCESS) || srcode.equals(ServerResponseCode.REGISTER_SUCCESS)) {

                    Platform.runLater(() -> {
                        try {
                            System.out.println("Launching the main window...");
                            launchChatWindow(serverConn);
                        } catch (IOException e) { e.printStackTrace(); }
                    });
                }
                else
                    Platform.runLater(() -> infoMessage.set(srcode.toString()));
            }
        });
    }


    public static boolean isUsernameInvalid(String username) {

        return username.length() < 3 || username.length() > 64 || !username.matches("\\w+");
    }

    public static boolean isPasswordInvalid(String password) {

        return password.length() < 6 || password.length() > 70; // max pw length is 72 characters for blowfish cypher
    }


    public void attemptLogin(String username, String password, boolean register) throws Exception {

        if (LoginModel.isPasswordInvalid(password) || LoginModel.isUsernameInvalid(username))
            return;

        System.out.println("Attempting to " + (register ? "register" : "log in") + " as " + username + " with " + password);
        serverConn.send(new LoginData(username, password, register));
    }

    private void launchChatWindow(ServerConnection serverConn) throws IOException {

        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ba/unsa/etf/rpr/chatapp/client/fxml/mainWindow.fxml"));

        MainWindowController mainWindowController = new MainWindowController(serverConn); // fieldUsername.getText()
        loader.setController(mainWindowController); // ako se ovo koristi, ne treba biti u .fxml fajlu fx:controller=""

        stage.setTitle("ChatApp - RPR");
        stage.setScene(new Scene(loader.load(), USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
        stage.setOnHidden(e -> {
            try {
                mainWindowController.shutdown();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        stage.show();
    }

    public String getInfoMessage() {
        return infoMessage.get();
    }

    public SimpleStringProperty infoMessageProperty() {
        return infoMessage;
    }

    public void setInfoMessage(String infoMessage) {
        this.infoMessage.set(infoMessage);
    }
}
