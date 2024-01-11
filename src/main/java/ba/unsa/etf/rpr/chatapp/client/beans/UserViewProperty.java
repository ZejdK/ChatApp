package ba.unsa.etf.rpr.chatapp.client.beans;

import ba.unsa.etf.rpr.chatapp.shared.dto.UserView;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

public class UserViewProperty {

    private final SimpleLongProperty id;
    private final SimpleStringProperty username;
    private final SimpleStringProperty roleList;

    public UserViewProperty(long id, String username, String roleList) {

        this.id = new SimpleLongProperty(id);
        this.username = new SimpleStringProperty(username);
        this.roleList = new SimpleStringProperty(roleList);
    }

    public UserViewProperty(UserView u) {

        this.id = new SimpleLongProperty(u.id());
        this.username = new SimpleStringProperty(u.username());
        this.roleList = new SimpleStringProperty(u.roleList());
    }

    public long getId() {
        return id.get();
    }

    public SimpleLongProperty idProperty() {
        return id;
    }

    public void setId(long id) {
        this.id.set(id);
    }

    public String getUsername() {
        return username.get();
    }

    public SimpleStringProperty usernameProperty() {
        return username;
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public String getRoleList() {
        return roleList.get();
    }

    public SimpleStringProperty roleListProperty() {
        return roleList;
    }

    public void setRoleList(String roleList) {
        this.roleList.set(roleList);
    }
}
