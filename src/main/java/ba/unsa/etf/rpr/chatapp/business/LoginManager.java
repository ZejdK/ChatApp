package ba.unsa.etf.rpr.chatapp.business;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class LoginManager {

    private final HashMap<String, String> serverResponseMessages;

    public LoginManager() {

        serverResponseMessages = new HashMap<>();
        serverResponseMessages.put("login_success", "Successfully logged in!");
        serverResponseMessages.put("login_invalid", "Invalid password specified for this username");
        serverResponseMessages.put("login_notfound", "Requested username not found");
        serverResponseMessages.put("register_success", "Successfully registered a new account");
        serverResponseMessages.put("register_taken", "Requested username is already in use");
    }

    public static boolean isUsernameInvalid(String username) {

        return username.length() < 3 || username.length() > 64 || !username.matches("\\w+");
    }

    public static boolean isPasswordInvalid(String password) {

        return password.length() < 6 || password.length() > 70; // max pw length is 72 characters for blowfish cypher
    }

    // todo: put in a separate thread
    public String attemptLogin(ServerConnectionManager serverConn, String username, String password) {

        System.out.println("Attempting to login as " + username + " with " + password);

        try {

            String loginLine = String.format("{\"type\":\"%s\",\"username\":\"%s\",\"password\":\"%s\"}", "login", username, password);
            String serverResponse = serverConn.sendText(loginLine); // stops the thread to wait for response

            Map<String, Object> map = (new ObjectMapper()).readValue(serverResponse, new TypeReference<>(){});
            return (String) map.get("status");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("EXCEPTION CAUGHT WHILE LOGGING IN\n");
            return null;
        }
    }
}
