package ba.unsa.etf.rpr.chatapp;

import ba.unsa.etf.rpr.chatapp.business.LoginManager;
import ba.unsa.etf.rpr.chatapp.business.ServerConnectionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class LoginWindowController {

    public Button loginWindow_registerButtonId;
    public Button loginWindow_loginButtonId;
    public TextField loginWindow_usernameInputId;
    public Label loginWindow_usernameErrorLabel;
    public PasswordField loginWindow_passwordInputId;
    public Label loginWindow_passwordErrorLabel;

    private final LoginManager loginManager;


    public LoginWindowController() {

        loginManager = new LoginManager();
    }


    @FXML
    protected void initialize() {

        loginWindow_usernameInputId.textProperty().addListener((observableValue, o, n) -> {

            if (n.length() == 0)
                loginWindow_usernameErrorLabel.setText("Username must not be empty");
            else if (LoginManager.isUsernameInvalid(n))
                loginWindow_usernameErrorLabel.setText("Invalid characters in username");
            else
                loginWindow_usernameErrorLabel.setText("");
        });

        loginWindow_passwordInputId.textProperty().addListener((observableValue, o, n) -> {

            // todo: add css colors
            if (!LoginManager.isPasswordValid(n))
                loginWindow_passwordErrorLabel.setText("Password must be at least 6 characters");
            else
                loginWindow_passwordErrorLabel.setText("");
            // to do: // else if (!passwordCheck(n)) // password must contain at least one lower case letter, one upper case letter and one digit
        });
    }


    public void onLoginWindowRegisterButtonClick(ActionEvent actionEvent) {

        String username = loginWindow_usernameInputId.getText();

        System.out.println("Attempting to register as " + username);

        if (LoginManager.isUsernameInvalid(username))
            return;

        // todo: provjera da li je ispravan korisnik

        System.out.println("registered in user " + username);
        System.out.println("Treba pritisnuti dugme Login - privremena funkcionalnost");
    }

    public void onLoginWindowLoginButtonClick(ActionEvent actionEvent) throws IOException {

        String username = loginWindow_usernameInputId.getText();
        String password = loginWindow_passwordInputId.getText();

        ServerConnectionManager serverConn = new ServerConnectionManager();

        if (!loginManager.attemptLogin(serverConn, username, password))
            return;

        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("mainWindow.fxml"));

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

        Stage loginProzor = (Stage) loginWindow_usernameInputId.getScene().getWindow();
        loginProzor.close();
    }
}
