package ba.unsa.etf.rpr.chatapp;

import ba.unsa.etf.rpr.chatapp.business.LoginManager;
import ba.unsa.etf.rpr.chatapp.business.ServerConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginWindowController {

    public Button loginWindow_registerButtonId;
    public Button loginWindow_loginButtonId;
    public TextField loginWindow_usernameInputId;
    public PasswordField loginWindow_passwordInputId;
    public Label loginWindow_infoLabel;

    private final LoginManager loginManager;

    public LoginWindowController(ServerConnection serverConn) {

        loginManager = new LoginManager(serverConn);
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

        loginWindowExecuteButtonAction(true);
    }

    public void onLoginWindowLoginButtonClick(ActionEvent actionEvent) {

        loginWindowExecuteButtonAction(false);
    }

    private void loginWindowExecuteButtonAction(boolean register) {

        try {

            String username = loginWindow_usernameInputId.getText();
            String password = loginWindow_passwordInputId.getText();
            loginManager.attemptLogin(username, password, register);

        } catch (Exception e) {

            e.printStackTrace();
            System.out.println("Exception caught while " + (register ? "registering" : "logging in") + "!");
        }
    }
}
