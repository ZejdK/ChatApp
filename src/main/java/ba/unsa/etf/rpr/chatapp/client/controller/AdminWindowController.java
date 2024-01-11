package ba.unsa.etf.rpr.chatapp.client.controller;

import ba.unsa.etf.rpr.chatapp.client.beans.UserViewProperty;
import ba.unsa.etf.rpr.chatapp.client.model.AdminModel;
import ba.unsa.etf.rpr.chatapp.shared.dto.UserCollection;
import ba.unsa.etf.rpr.chatapp.shared.dto.UserView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

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

    public AdminWindowController(AdminModel a) {

        adminModel = a;
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

        infoLabel.setText("Received data");
        for (UserView u : users.users())
            userTableData.add(new UserViewProperty(u));
        infoLabel.setText("Processed data");
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

    public void onRefreshUsersAction(ActionEvent actionEvent) {

        infoLabel.setText("refreshing users");
    }

    public void shutdown() {

        System.out.println("Shutting down admin window...");
    }
}
