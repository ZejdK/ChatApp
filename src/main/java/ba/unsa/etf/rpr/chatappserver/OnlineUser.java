package ba.unsa.etf.rpr.chatappserver;

import at.favre.lib.crypto.bcrypt.BCrypt;
import ba.unsa.etf.rpr.*;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.function.Consumer;

public class OnlineUser {

    private static int userCounter = 0;

    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;

    private boolean loggedIn;
    private boolean valid;

    private String nickname;
    private User user;
    private Thread listener;

    private final Consumer<ChatMessage> onMessageCallback;
    private final Consumer<String> onEventCallback;

    public OnlineUser(Socket socket, Consumer<ChatMessage> onMessageCallback, Consumer<String> onEventCallback) throws IOException {

        this.socket = socket;
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());

        loggedIn = false;
        valid = true;
        nickname = "user " + userCounter;
        userCounter++;

        this.onMessageCallback = onMessageCallback;
        this.onEventCallback = onEventCallback;

        onEventCallback.accept(nickname + " is connecting...");
    }


    // this method is executed in an infinite loop inside startListener
    private void ThreadLogic() throws IOException, ClassNotFoundException, UserDisconnectedException, SQLException {

        ValidityCheck();

        Object o = in.readObject();
        System.out.println("Read from " + nickname + o);

        if (!loggedIn) {

            System.out.println("received obj from a user who is not logged in of type " + o);
            if (o instanceof LoginData loginData) {

                System.out.println("User wants to log in with data " + loginData);

                User user = VerifyLogin(loginData);

                if (user != null) {

                    System.out.println("User has successfully logged in!");
                    loggedIn = true;
                    this.user = user;
                    onEventCallback.accept(nickname + " has joined the channel");
                    nickname = user.getUsername();
                }
            }
        }
        else
            ProcessUserInput(o);
    }


    public User VerifyLogin(LoginData loginData) throws IOException, UserDisconnectedException {

        User user = UserDao.getInstance().get(loginData.username);

        if (loginData.isRegistering) {

            if (user == null) {

                User newUser = UserDao.getInstance().add(loginData);
                if (newUser != null) {

                    send(ServerResponseCode.REGISTER_SUCCESS);
                    return newUser;
                }
                else {

                    send(ServerResponseCode.REGISTER_UNKNOWNFAIL);
                    return null;
                }

            }
            else {
                send(ServerResponseCode.REGISTER_USERNAMETAKEN);
                return null;
            }
        }
        else {

            if (user == null) {

                send(ServerResponseCode.LOGIN_USERNAMENOTFOUND);
                return null;
            }

            BCrypt.Result result = BCrypt.verifyer().verify(loginData.password.toCharArray(), user.getPasswordHash());
            if (result.verified) {

                send(ServerResponseCode.LOGIN_SUCCESS);
                return user;
            }
            else {

                send(ServerResponseCode.LOGIN_INVALIDPASSWORD);
                return null;
            }
        }
    }


    // this function assumes it will be called upon a valid user who is logged in
    public void ProcessUserInput(Object o) {

        if (o instanceof ChatInput chatInput) {

            System.out.println("MSG from user " + nickname + ": " + chatInput);
            onMessageCallback.accept(new ChatMessage(nickname, chatInput.content));
        }
    }


    public void startListener() {

        listener = new Thread(() -> {
            try {
                while (true) {
                    ThreadLogic();
                }
            } catch (IOException | ClassNotFoundException | UserDisconnectedException | SQLException e) {
                e.printStackTrace();
                valid = false;
            }
        });

        listener.start();
    }


    public void send(Object o) throws IOException, UserDisconnectedException {

        ValidityCheck();

        System.out.println("Attempting to send to " + (loggedIn ? user.getInfo() : nickname) + "data: " + o);
        out.writeObject(o);
        out.flush();
    }


    public void drop() throws IOException, InterruptedException, UserDisconnectedException {

        ValidityCheck();

        out.close();
        in.close();
        socket.close();
        listener.join();
        valid = false;
        onEventCallback.accept(user.getInfo() + " has left the channel");
        System.out.println(user.getInfo() + " HAS LEFT THE CHANNEL AND WAS INVALIDATED");

        // eventually clean everything up in the database and stuff
    }


    public void ValidityCheck() throws UserDisconnectedException {

        if (!valid)
            throw new UserDisconnectedException("Accessed user is no longer connected to the server!");
    }
}
