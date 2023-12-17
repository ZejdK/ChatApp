package ba.unsa.etf.rpr.chatapp;

import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.*;


import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class LoginWindowController {

    public Button loginWindow_registerButtonId;
    public Button loginWindow_loginButtonId;
    public TextField loginWindow_usernameInputId;
    public Label loginWindow_usernameErrorLabel;
    public PasswordField loginWindow_passwordInputId;
    public Label loginWindow_passwordErrorLabel;
    HashMap<String, String> serverResponseMessages;

    @FXML
    protected void initialize() {

        serverResponseMessages = new HashMap<>();
        serverResponseMessages.put("login_success", "Successfully logged in!");
        serverResponseMessages.put("login_invalid", "Invalid password specified for this username");
        serverResponseMessages.put("login_notfound", "Requested username not found");
        serverResponseMessages.put("register_success", "Successfully registered a new account");
        serverResponseMessages.put("register_taken", "Requested username is already in use");

        loginWindow_usernameInputId.textProperty().addListener((observableValue, o, n) -> {

            if (n.length() == 0)
                loginWindow_usernameErrorLabel.setText("Username must not be empty");
            else if (isUsernameInvalid(n))
                loginWindow_usernameErrorLabel.setText("Invalid characters in username");
            else
                loginWindow_usernameErrorLabel.setText("");
        });

        loginWindow_passwordInputId.textProperty().addListener((observableValue, o, n) -> {

            // todo: add css colors
            if (!isPasswordValid(n))
                loginWindow_passwordErrorLabel.setText("Password must be at least 6 characters");
            else
                loginWindow_passwordErrorLabel.setText("");
            // to do: // else if (!passwordCheck(n)) // password must contain at least one lower case letter, one upper case letter and one digit
        });
    }

    private boolean isUsernameInvalid(String username) {

        return username.length() > 3 && username.length() < 64 && !username.matches("\\w+");
    }

    private boolean isPasswordValid(String password) {

        return password.length() > 5 && password.length() < 70; // max pw length is 72 characters for blowfish cypher
    }

    public void onLoginWindowRegisterButtonClick(ActionEvent actionEvent) throws IOException {

        String username = loginWindow_usernameInputId.getText();

        System.out.println("Attempting to register as " + username);

        if (isUsernameInvalid(username))
            return;

        // todo: provjera da li je ispravan korisnik

        System.out.println("registered in user " + username);
        System.out.println("Treba pritisnuti dugme Login - privremena funkcionalnost");
    }

    public void onLoginWindowLoginButtonClick(ActionEvent actionEvent) throws IOException {

        String username = loginWindow_usernameInputId.getText();
        String password = loginWindow_passwordInputId.getText();
        System.out.println("Attempting to register as " + username + " with " + password);

        if (isUsernameInvalid(username) && isPasswordValid(password))
            return;

        String pathname = "C:\\Users\\Zejd\\IdeaProjects\\RPR\\Projekt\\ChatApp\\src\\main\\resources\\ba\\unsa\\etf\\rpr\\chatapp\\configclient.json";
        ClientConfigDao clientConfigDao = (new ObjectMapper()).readValue(new File(pathname), ClientConfigDao.class);

        // todo: prebaciti vodjenje racuna o prozoru i soketu u Main klasu
        Socket cSocket = new Socket(clientConfigDao.getServerUrl(), clientConfigDao.getServerPort());
        PrintWriter out = new PrintWriter(cSocket.getOutputStream()); // ObjectOutputStream out = cSocket.getOutputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(cSocket.getInputStream()));

        try {
            String loginLine = String.format("{\"type\":\"%s\",\"username\":\"%s\",\"password\":\"%s\"}", "login", username, password);

            System.out.println("Sending: " + loginLine);
            out.println(loginLine);
            out.flush();

            System.out.println("Awaiting server response...");
            String serverResponse = in.readLine();
            System.out.println("Server responsed with " + serverResponse);

            // todo: privremeno rijesenje; treba biti vise grananja
            if (serverResponse.equals("login_success")) {

                cSocket.close();
                System.exit(0);
                throw new RuntimeException(serverResponseMessages.get(serverResponse));
            }

        } catch (Exception e) { e.printStackTrace(); System.out.println("EXCEPTION CAUGHT WHILE LOGGING IN\n"); }

        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("mainWindow.fxml"));

        MainWindowController mainWindowController = new MainWindowController(cSocket, out, in); // fieldUsername.getText()
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

        Stage loginProzor = (Stage) loginWindow_usernameInputId.getScene().getWindow();
        loginProzor.close();
    }
}
