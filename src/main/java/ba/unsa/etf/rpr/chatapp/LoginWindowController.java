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
    public PasswordField loginWindow_passwordInputId;
    public Label loginWindow_infoLabel;

    private final LoginManager loginManager;


    public LoginWindowController() {

        loginManager = new LoginManager();
    }


    @FXML
    protected void initialize() {

        loginWindow_usernameInputId.textProperty().addListener((observableValue, o, n) -> {

            if (n.length() == 0)
                loginWindow_infoLabel.setText("Username must not be empty");
            else if (LoginManager.isUsernameInvalid(n))
                loginWindow_infoLabel.setText("Invalid username");
            else
                loginWindow_infoLabel.setText("");
        });

        loginWindow_passwordInputId.textProperty().addListener((observableValue, o, n) -> {

            // todo: add css colors
            if (LoginManager.isPasswordInvalid(n))
                loginWindow_infoLabel.setText("Password must be at least 6 characters");
            else
                loginWindow_infoLabel.setText("");
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

        if (LoginManager.isPasswordInvalid(password) || LoginManager.isUsernameInvalid(username))
            return;

        ServerConnectionManager serverConn = new ServerConnectionManager();

        String loginResult = loginManager.attemptLogin(serverConn, username, password, false);
        System.out.println("loginResult is " + loginResult);

        if (loginResult == null) {

            System.out.println("Warning: Received empty login response from the server");
        }
        else if (loginResult.equals("login_success")) {

            loginWindow_infoLabel.setText("Successfully logged in...");
            launchChatWindow(serverConn);
        }
        else if (loginResult.equals("login_invalid")) {

            loginWindow_infoLabel.setText("Invalid password");
        }
        else if (loginResult.equals("login_notfound")) {

            loginWindow_infoLabel.setText("Username doesn't exist");
        }
        else {

            System.out.println("Warning! Unknown login result value returned: " + loginResult);
        }
    }

    private void launchChatWindow(ServerConnectionManager serverConn) throws IOException {

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
