package ba.unsa.etf.rpr.chatapp.shared.dto;

import java.io.Serializable;

public class LoginData implements Serializable {

    public String username;
    public String password;
    public boolean isRegistering;

    public LoginData(String username, String password, boolean isRegistering) {
        this.username = username;
        this.password = password;
        this.isRegistering = isRegistering;
    }

    @Override
    public String toString() {

        return username + " " + password;
    }
}

