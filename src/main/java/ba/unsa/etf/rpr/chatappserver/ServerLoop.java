package ba.unsa.etf.rpr.chatappserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerLoop {

    private ServerSocket serverSocket;
    private ArrayList<OnlineUser> onlineUsers;
    private DatabaseConnection databaseConnection;

    public void startServer(int port) throws IOException {

        DatabaseConnection dbConn = DatabaseConnection.getInstance();

        onlineUsers = new ArrayList<>();
        serverSocket = new ServerSocket(port);
        System.out.println("Started the server on the port " + port);

        while (true) {
            try {
                processConnection();
            } catch (Exception e) { e.printStackTrace(); System.out.println("Exception caught!"); }
        }
    }

    private void processConnection() throws IOException {

        Socket client = serverSocket.accept();
        System.out.println("Incoming connection from " + client.getRemoteSocketAddress() + ":" + client.getPort());

        try {
            OnlineUser u = new OnlineUser(client, this::broadcast, this::inform);

            u.startListener();
            onlineUsers.add(u);

        } catch (Exception e) { e.printStackTrace(); }
    }

    // send object passed as a parameter to all online users
    public void broadcast(Object o) {

        onlineUsers.forEach((user) -> {

            try {
                user.send(o);
            } catch (IOException e) { e.printStackTrace(); }
            catch (UserDisconnectedException e) { e.printStackTrace(); System.out.println("ERROR: User is no longer connected"); }
        });
    }

    // sends a message
    public void inform(String message) {

        broadcast(message);
    }

}
