package ba.unsa.etf.rpr.chatapp.client.controller;

import ba.unsa.etf.rpr.chatapp.client.model.LoginModel;
import ba.unsa.etf.rpr.chatapp.client.business.ServerConnection;
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

    private final LoginModel loginModel;


    public LoginWindowController(ServerConnection serverConn) {

        loginModel = new LoginModel(serverConn);
    }


    @FXML
    protected void initialize() {

        loginWindow_infoLabel.textProperty().bind(loginModel.infoMessageProperty());

        loginWindow_usernameInputId.textProperty().addListener((observableValue, o, n) -> {

            if (n.length() == 0)
                loginModel.setInfoMessage("Username must not be empty");
            else if (LoginModel.isUsernameInvalid(n))
                loginModel.setInfoMessage("Invalid username");
            else
                loginModel.setInfoMessage("");
        });

        loginWindow_passwordInputId.textProperty().addListener((observableValue, o, n) -> {

            // todo: add css colors
            if (LoginModel.isPasswordInvalid(n))
                loginModel.setInfoMessage("Password must be at least 6 characters");
            else
                loginModel.setInfoMessage("");
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
            loginModel.attemptLogin(username, password, register);

        } catch (Exception e) {

            e.printStackTrace();
            System.out.println("Exception caught while " + (register ? "registering" : "logging in") + "!");
        }
    }
}
