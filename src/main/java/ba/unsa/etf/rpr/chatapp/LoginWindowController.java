package ba.unsa.etf.rpr.chatapp;

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

    @FXML
    protected void initialize() {

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
            if (n.length() < 6)
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
        // za sada pretpostavimo da jeste

        System.out.println("Registered user " + username);


        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("mainWindow.fxml"));

        MainWindowController noviprozctrlr = new MainWindowController(); // fieldUsername.getText()
        loader.setController(noviprozctrlr); // ako se ovo koristi, ne treba biti u .fxml fajlu fx:controller=""

        stage.setTitle("ChatApp - RPR");
        stage.setScene(new Scene(loader.load(), USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
        stage.show();

        Stage loginProzor = (Stage) loginWindow_usernameInputId.getScene().getWindow();
        loginProzor.close();
    }

    public void onLoginWindowLoginButtonClick(ActionEvent actionEvent) {

        String username = loginWindow_usernameInputId.getText();

        System.out.println("Attempting to register as " + username);

        if (isUsernameInvalid(username))
            return;

        // todo: provjera da li je ispravan korisnik

        System.out.println("Logged in user " + username);
        System.out.println("Treba pritisnuti dugme Register - privremena funkcionalnost");
    }
}
