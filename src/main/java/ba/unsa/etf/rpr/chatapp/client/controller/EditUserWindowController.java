package ba.unsa.etf.rpr.chatapp.client.controller;

import ba.unsa.etf.rpr.chatapp.shared.dto.UserView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.util.Random;
import java.util.function.BiConsumer;

public class EditUserWindowController {

    public TextField usernameField;
    public TextField passwordField;
    public TextField rolesField;
    public Button buttonConfirm;

    private final BiConsumer<UserView, String> actionConsumer;

    public EditUserWindowController(BiConsumer<UserView, String> actionConsumer) {

        this.actionConsumer = actionConsumer;
    }

    @FXML
    public void initialize() {

        // usernameField.textProperty().addListener((obs, oldValue, newValue) -> {});
        // TODO: add data checks
    }

    public void setFields(String username, String roleList) {

        usernameField.setText(username);
        passwordField.setText("");
        rolesField.setText(roleList);
    }

    public void clearFields() {

        usernameField.clear();
        passwordField.clear();
        rolesField.clear();
    }

    public void onConfirmAction(ActionEvent actionEvent) {

        // TODO: add data checks

        actionConsumer.accept(new UserView(0, usernameField.getText(), rolesField.getText()), passwordField.getText());
    }

    public void onGeneratePassword(ActionEvent actionEvent) {

        Random rand = new Random();
        String randomString = rand.ints(48, 122) // 48 = '0', 122 = 'z'
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(10)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        passwordField.setText(randomString);
    }
}
