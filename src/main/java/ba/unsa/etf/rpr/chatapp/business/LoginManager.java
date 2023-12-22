package ba.unsa.etf.rpr.chatapp.business;

import java.util.HashMap;

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
    public boolean attemptLogin(ServerConnectionManager serverConn, String username, String password) {

        System.out.println("Attempting to register as " + username + " with " + password);

        if (isUsernameInvalid(username) || isPasswordInvalid(password))
            return false;

        try {

            String loginLine = String.format("{\"type\":\"%s\",\"username\":\"%s\",\"password\":\"%s\"}", "login", username, password);
            String serverResponse = serverConn.sendText(loginLine); // stops the thread to wait for response

            // todo: privremeno rijesenje; treba biti vise grananja
            if (!serverResponse.equals("login_success")) {

                serverConn.close();
                System.exit(0);
                throw new RuntimeException(serverResponseMessages.get(serverResponse));
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("EXCEPTION CAUGHT WHILE LOGGING IN\n");
            return false;
        }

        return true;
    }
}
