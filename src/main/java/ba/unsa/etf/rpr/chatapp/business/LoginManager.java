package ba.unsa.etf.rpr.chatapp.business;

import ba.unsa.etf.rpr.chatapp.ClientConfigDao;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;

public class LoginManager {

    private HashMap<String, String> serverResponseMessages;

    public LoginManager() {

        serverResponseMessages = new HashMap<>();
        serverResponseMessages.put("login_success", "Successfully logged in!");
        serverResponseMessages.put("login_invalid", "Invalid password specified for this username");
        serverResponseMessages.put("login_notfound", "Requested username not found");
        serverResponseMessages.put("register_success", "Successfully registered a new account");
        serverResponseMessages.put("register_taken", "Requested username is already in use");
    }

    public static boolean isUsernameInvalid(String username) {

        return username.length() > 3 && username.length() < 64 && !username.matches("\\w+");
    }

    public static boolean isPasswordValid(String password) {

        return password.length() > 5 && password.length() < 70; // max pw length is 72 characters for blowfish cypher
    }

    // todo: put in a separate thread
    public boolean attemptLogin(String username, String password) {

        System.out.println("Attempting to register as " + username + " with " + password);

        if (isUsernameInvalid(username) && isPasswordValid(password))
            return false;

        try {

            Socket cSocket = new Socket(ClientConfigDao.getInstance().getServerUrl(), ClientConfigDao.getInstance().getServerPort());
            PrintWriter out = new PrintWriter(cSocket.getOutputStream()); // ObjectOutputStream out = cSocket.getOutputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(cSocket.getInputStream()));

            String loginLine = String.format("{\"type\":\"%s\",\"username\":\"%s\",\"password\":\"%s\"}", "login", username, password);

            System.out.println("Sending: " + loginLine);
            out.println(loginLine);
            out.flush();

            System.out.println("Awaiting server response...");
            String serverResponse = in.readLine();
            System.out.println("Server responsed with " + serverResponse);

            // todo: privremeno rijesenje; treba biti vise grananja
            if (serverResponse.equals("login_success")) {

                cSocket.close();
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
