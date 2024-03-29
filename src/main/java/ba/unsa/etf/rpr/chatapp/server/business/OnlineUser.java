package ba.unsa.etf.rpr.chatapp.server.business;

import at.favre.lib.crypto.bcrypt.BCrypt;
import ba.unsa.etf.rpr.chatapp.server.beans.Role;
import ba.unsa.etf.rpr.chatapp.server.beans.User;
import ba.unsa.etf.rpr.chatapp.server.dao.RoleDao;
import ba.unsa.etf.rpr.chatapp.server.exceptions.UserDisconnectedException;
import ba.unsa.etf.rpr.chatapp.server.dao.UserDao;
import ba.unsa.etf.rpr.chatapp.shared.dto.*;
import ba.unsa.etf.rpr.chatapp.shared.dto.command.AddUserCommand;
import ba.unsa.etf.rpr.chatapp.shared.dto.command.DeleteUserCommand;
import ba.unsa.etf.rpr.chatapp.shared.dto.command.EditUserCommand;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class OnlineUser {

    private static int userCounter = 0;

    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;

    private boolean loggedIn;
    private boolean valid;

    private String nickname;
    private User user;
    private ArrayList<Role> roles;
    private Thread listener;

    private final Consumer<ChatMessage> onMessageCallback;
    private final Consumer<ChatMessage> onEventCallback;

    public OnlineUser(Socket socket, Consumer<ChatMessage> onMessageCallback, Consumer<ChatMessage> onEventCallback) throws IOException {

        this.socket = socket;
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());

        loggedIn = false;
        valid = true;
        nickname = "user " + userCounter;
        userCounter++;

        this.onMessageCallback = onMessageCallback;
        this.onEventCallback = onEventCallback;

        onEventCallback.accept(new ChatMessage("SERVER", nickname + " is connecting..."));
    }

    // this method is executed in an infinite loop inside startListener
    private void threadLogic() throws IOException, ClassNotFoundException, UserDisconnectedException, SQLException {

        validityCheck();

        Object o = in.readObject();
        System.out.println("Read from " + nickname + o);

        if (!loggedIn) {

            System.out.println("received obj from a user who is not logged in of type " + o);
            if (o instanceof LoginData loginData) {

                System.out.println("User wants to log in with data " + loginData);

                User user = verifyUser(loginData);

                if (user != null) {

                    ArrayList<Long> userRoles = user.getRoles();
                    System.out.println("User has successfully logged in!");
                    loggedIn = true;
                    this.user = user;
                    this.roles = (userRoles.isEmpty()) ? new ArrayList<>() : RoleDao.getInstance().get(userRoles);
                    nickname = user.getUsername();
                    onEventCallback.accept(new ChatMessage("SERVER", nickname + " has joined the channel"));
                }
            }
        }
        else
            processUserInput(o);
    }

    private User verifyUserRegister(User user, LoginData loginData) throws IOException, UserDisconnectedException {

        if (user != null) {

            send(ServerResponseCode.REGISTER_USERNAMETAKEN);
            return null;
        }

        User newUser = UserDao.getInstance().add(new User(0, loginData.username, loginData.password));
        if (newUser != null) {

            send(ServerResponseCode.REGISTER_SUCCESS);
            return newUser;
        }
        else {

            send(ServerResponseCode.REGISTER_UNKNOWNFAIL);
            return null;
        }
    }

    private User verifyUserLogin(User user, LoginData loginData) throws IOException, UserDisconnectedException {

        if (user == null) {

            send(ServerResponseCode.LOGIN_USERNAMENOTFOUND);
            return null;
        }

        BCrypt.Result result = BCrypt.verifyer().verify(loginData.password.toCharArray(), user.getPassword());
        if (result.verified) {

            send(ServerResponseCode.LOGIN_SUCCESS);
            return user;
        }
        else {

            send(ServerResponseCode.LOGIN_INVALIDPASSWORD);
            return null;
        }
    }

    public User verifyUser(LoginData loginData) throws IOException, UserDisconnectedException {

        User user = UserDao.getInstance().get(loginData.username);

        if (loginData.isRegistering)
            return verifyUserRegister(user, loginData);
        else
            return verifyUserLogin(user, loginData);
    }

    private boolean isUserAdmin() {

        // TODO: 1 and 2 are, for now, hardcoded to admin and moderator roles
        return roles.stream().anyMatch(r -> r.getId() == 1 || r.getId() == 2);
    }

    // this function assumes it will be called upon a valid user who is logged in
    public void processUserInput(Object o) {

        if (o instanceof ChatInput chatInput) {

            System.out.println("MSG from user " + nickname + ": " + chatInput);
            onMessageCallback.accept(new ChatMessage(nickname, chatInput.content()));
        }
        else {

            if (!isUserAdmin()) {

                // log this
                System.out.println("Attempted to request serverRequest without enough privileges");
                return;
            }

            if (o instanceof ServerRequest serverRequest)
                processServerRequest(serverRequest);
            else if (o instanceof AddUserCommand addUserCommand)
                processAddUserCommand(addUserCommand);
            else if (o instanceof EditUserCommand editUserCommand)
                processEditUserCommand(editUserCommand);
            else if (o instanceof DeleteUserCommand deleteUserCommand)
                processDeleteCommand(deleteUserCommand);
        }
    }

    private void processServerRequest(ServerRequest serverRequest) {

        if (serverRequest == ServerRequest.ADMINREQUEST_GETUSERS)

            try {

                ArrayList<User> users = UserDao.getInstance().getAll();

                List<UserView> userViewList = users.stream().map(u -> {

                    String roleString = u.getRoles().stream()
                            .map(roleId -> RoleDao.getInstance().get(roleId).getName())
                            .collect(Collectors.joining(","));

                    return new UserView(u.getId(), u.getUsername(), roleString);
                }).toList();

                send(new UserCollection(userViewList));

            } catch (Exception e) { e.printStackTrace(); }
    }

    private void processAddUserCommand(AddUserCommand addUserCommand) {

        User newUser = new User(0, addUserCommand.username(), addUserCommand.password());

        //ArrayList<Long> roleIds = RoleDao.getInstance().getRoleIds(addUserCommand.roleList());
        //newUser.setRoles(roleIds);

        try {

            UserDao.getInstance().add(newUser);
            send(ServerResponseCode.USERCRUD_SUCCESS);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void processEditUserCommand(EditUserCommand editUserCommand) {

        User editedUser = new User(editUserCommand.id(), editUserCommand.newUsername(), editUserCommand.newRoleList());

        try {

            UserDao.getInstance().update(editedUser);
            send(ServerResponseCode.USERCRUD_SUCCESS);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void processDeleteCommand(DeleteUserCommand deleteUserCommand) {

        try {

            System.out.println("Deleting user with id " + deleteUserCommand.id());
            UserDao.getInstance().delete(deleteUserCommand.id());
            send(ServerResponseCode.USERCRUD_SUCCESS);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void startListener() {

        listener = new Thread(() -> {
            try {
                while (true) {
                    threadLogic();
                }
            } catch (IOException | ClassNotFoundException | UserDisconnectedException | SQLException e) {
                e.printStackTrace();
                valid = false;
            }
        });

        listener.start();
    }

    public void send(Object o) throws IOException, UserDisconnectedException {

        validityCheck();

        System.out.println("Attempting to send to " + (loggedIn ? user.getInfo() : nickname) + "data: " + o);
        out.writeObject(o);
        out.flush();
    }

    public void drop() throws IOException, InterruptedException, UserDisconnectedException {

        validityCheck();

        out.close();
        in.close();
        socket.close();
        listener.join();
        valid = false;
        onEventCallback.accept(new ChatMessage("SERVER", user.getInfo() + " has left the channel"));
        System.out.println(user.getInfo() + " HAS LEFT THE CHANNEL AND WAS INVALIDATED");

        // eventually clean everything up in the database and stuff
    }

    public void validityCheck() throws UserDisconnectedException {

        if (!valid)
            throw new UserDisconnectedException("Accessed user is no longer connected to the server!");
    }
}
