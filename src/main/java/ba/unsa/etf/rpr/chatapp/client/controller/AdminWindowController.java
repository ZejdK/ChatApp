package ba.unsa.etf.rpr.chatapp.client.controller;

import ba.unsa.etf.rpr.chatapp.client.beans.UserViewProperty;
import ba.unsa.etf.rpr.chatapp.client.model.AdminModel;
import ba.unsa.etf.rpr.chatapp.shared.dto.UserCollection;
import ba.unsa.etf.rpr.chatapp.shared.dto.UserView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class AdminWindowController {

    public TableView<UserViewProperty> userTable;
    public TableColumn<UserViewProperty, Long> userIdColumn;
    public TableColumn<UserViewProperty, String> usernameColumn;
    public TableColumn<UserViewProperty, String> rolesColumn;

    public Button addUser;
    public Button editUser;
    public Button deleteUser;
    public Button editRoles;

    public Label infoLabel;

    private final AdminModel adminModel;

    private ObservableList<UserViewProperty> userTableData;

    public AdminWindowController(ServerConnection serverConnection, AdminModel a) {

        this.adminModel = a;
        this.serverConnection = serverConnection;

        try {

            FXMLLoader loader = new FXMLLoader(ChatAppClient.class.getResource("fxml/editUser.fxml"));
            editUserWindowController = new EditUserWindowController(this::onUserInputFinished);
            loader.setController(editUserWindowController);

            userDataInputStage = new Stage();
            userDataInputStage.setTitle("Input user data");
            userDataInputStage.setScene(new Scene(loader.load(), USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            userDataInputStage.setResizable(false);
        } catch (Exception e ) { e.printStackTrace(); }
    }

    @FXML
    public void initialize() {

        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        rolesColumn.setCellValueFactory(new PropertyValueFactory<>("roleList"));

        userTableData = FXCollections.observableArrayList() ;
        userTable.setItems(userTableData);
    }

    public void setData(UserCollection users) {

        Platform.runLater(() -> {

            infoLabel.setText("Received data");
            for (UserView u : users.users())
                userTableData.add(new UserViewProperty(u));
            infoLabel.setText("Processed data");
        });
    }


    public void onDeleteUserAction(ActionEvent actionEvent) {

        infoLabel.setText("delete user");
    }

    public void onEditUserAction(ActionEvent actionEvent) {

        infoLabel.setText("edit user");
    }

    public void onAddUserAction(ActionEvent actionEvent) {

        infoLabel.setText("add user");
    }

    public void onUserInputFinished(UserView newUser, String password) {

        editUserWindowController.clearFields();
        userDataInputStage.close();
        statusLabel.setText("Sending user input data to server");

        try {

            if (currentlyEdited == null) // TODO: find a better solution
                serverConnection.send(new AddUserCommand(newUser.username(), password, newUser.roleList()));
            else
                // TODO: ne zaboraviti provjeru ako vec ima taj username
                serverConnection.send(new EditUserCommand(currentlyEdited.getId(), newUser.username(), newUser.roleList()));

        } catch (IOException e) { e.printStackTrace(); }

        currentlyEdited = null;
    }


    public void onRefreshUsersAction(ActionEvent actionEvent) {

        infoLabel.setText("refreshing users");
    }

    public void shutdown() {

        System.out.println("Shutting down admin window...");
    }
}
