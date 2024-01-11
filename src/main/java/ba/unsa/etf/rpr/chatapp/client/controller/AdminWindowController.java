package ba.unsa.etf.rpr.chatapp.client.controller;

import ba.unsa.etf.rpr.chatapp.client.ChatAppClient;
import ba.unsa.etf.rpr.chatapp.client.beans.UserViewProperty;
import ba.unsa.etf.rpr.chatapp.client.business.ServerConnection;
import ba.unsa.etf.rpr.chatapp.client.model.AdminModel;
import ba.unsa.etf.rpr.chatapp.shared.dto.ServerRequest;
import ba.unsa.etf.rpr.chatapp.shared.dto.ServerResponseCode;
import ba.unsa.etf.rpr.chatapp.shared.dto.UserCollection;
import ba.unsa.etf.rpr.chatapp.shared.dto.UserView;
import ba.unsa.etf.rpr.chatapp.shared.dto.command.AddUserCommand;
import ba.unsa.etf.rpr.chatapp.shared.dto.command.DeleteUserCommand;
import ba.unsa.etf.rpr.chatapp.shared.dto.command.EditUserCommand;
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

    public Label statusLabel;

    private final AdminModel adminModel;
    private final ServerConnection serverConnection;
    private Stage userDataInputStage;
    private EditUserWindowController editUserWindowController;
    private UserViewProperty currentlyEdited;

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

        serverConnection.addConsumer((Object o) -> {

            if (o instanceof ServerResponseCode serverResponseCode && serverResponseCode == ServerResponseCode.USERCRUD_SUCCESS) {

                updateStatusBar("User operation successfully executed");

                try {

                    serverConnection.send(ServerRequest.ADMINREQUEST_GETUSERS);
                } catch (Exception e) { System.out.println(e.getMessage()); }
            }
            else if (o instanceof UserCollection users) {

                System.out.println("[AWC] HI");
                setData(users);
            }
        });
    }

    public void setData(UserCollection users) {

        Platform.runLater(() -> {

            userTableData.clear();
            statusLabel.setText("Received data");
            for (UserView u : users.users())
                userTableData.add(new UserViewProperty(u));
            statusLabel.setText("Processed data");
        });
    }


    public void onDeleteUserAction(ActionEvent actionEvent) throws IOException {

        ObservableList<UserViewProperty> userSelected;
        userSelected = userTable.getSelectionModel().getSelectedItems();

        if (userSelected.size() == 0) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Selection error");
            alert.setHeaderText("No user was selected for deletion");
            alert.setContentText("");
            alert.showAndWait();

            return;
        }

        serverConnection.send(new DeleteUserCommand(userSelected.get(0).getId()));

        statusLabel.setText("Waiting for delete user input");
    }

    public void onEditUserAction(ActionEvent actionEvent) {

        ObservableList<UserViewProperty> userSelected;
        userSelected = userTable.getSelectionModel().getSelectedItems();

        if (userSelected.size() == 0) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Selection error");
            alert.setHeaderText("No user was selected for editing");
            alert.setContentText("");
            alert.showAndWait();

            return;
        }

        currentlyEdited = userSelected.get(0);
        editUserWindowController.setFields(currentlyEdited.getUsername(), currentlyEdited.getRoleList());

        userDataInputStage.show();
        statusLabel.setText("Waiting for edit user input");
    }

    public void onAddUserAction(ActionEvent actionEvent) {

        userDataInputStage.show();
        statusLabel.setText("Waiting for add user input");
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

        try {

            statusLabel.setText("Sending request to the server...");
            serverConnection.send(ServerRequest.ADMINREQUEST_GETUSERS);
            statusLabel.setText("Waiting for the server response...");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {

        System.out.println("Shutting down admin window...");
    }

    public void updateStatusBar(String text) {

        Platform.runLater(() -> statusLabel.setText(text));
    }
}
