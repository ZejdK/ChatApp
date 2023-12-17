package ba.unsa.etf.rpr.chatappserv;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConnectionManager {

    // todo vidjeti da li ovaj socket salje bez ikakve zastite? https://docs.oracle.com/javase/8/docs/api/javax/net/ssl/SSLSocket.html
    private ServerSocket serverSocket;
    private ArrayList<ActiveUser> clients;
    private ExecutorService ex;
    private final DatabaseConnectionManager dbm;

    public ConnectionManager(DatabaseConnectionManager dbm) {

        this.dbm = dbm;
    }

    public void onMessageSent(ChatMessage msg) {

        if (msg.content.charAt(0) == '/')
            ;// RunChatCommand(msg);
        else
            broadcast(msg);
    }

    public void broadcast(ChatMessage msg) {

        String msgOut = msg.username + ": " + msg.content;

        System.out.println(msgOut);
        clients.forEach(au -> au.sendMessage(msgOut)); // todo: poslati serijalizovan objekat umjesto plain text
    }

    // todo: fali mnogo koda za zastitu
    public void startServer(int port) throws IOException {

        ex = Executors.newCachedThreadPool();
        serverSocket = new ServerSocket(port);
        clients = new ArrayList<>();

        while (true) {

            Socket c = serverSocket.accept(); // stops the thread until a client connects

            PrintWriter out = new PrintWriter(c.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));

            String loginLine = in.readLine();
            System.out.println("Incoming connection with data: " + loginLine + " from " + c.getRemoteSocketAddress() + ":" + c.getPort());

            try {

                // todo: ideally, the user account should not be tied only to a username
                LoginDao loginInfo = (new ObjectMapper()).readValue(loginLine, LoginDao.class);
                UserDao userInfo = dbm.LookupUser(loginInfo.getUsername());
                ProcessUserLogin(loginInfo, userInfo, c, out);

            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    private void returnResponse(PrintWriter out, String status) {

        System.out.println("Returning response " + status);
        out.println("{\"status\":\"" + status + "\"}");
        out.flush();
    }

    private void LogInUser(UserDao user, Socket clientSocket) throws IOException {

        // todo: provjeriti ovo rjesenje za prosljedjivanje funkcija
        clients.add(new ActiveUser(clientSocket, user.getUsername(), this::onMessageSent, this::broadcast, this::broadcast));
        ex.execute(clients.get(clients.size() - 1));
        System.out.printf("User %s successfully logged in as %d from %s:%s", user.getUsername(), user.getDatabaseId(), clientSocket.getRemoteSocketAddress(), clientSocket.getPort());
    }

    private void LogAction(String msg) {

        // todo: some sort of action logging into the database and audit of said log
        System.out.println(msg);
    }

    private void ProcessUserLogin(LoginDao loginInfo, UserDao user, Socket clientSocket, PrintWriter out) throws IOException {

        // todo: add a check if the user is already logged in
        if (loginInfo.getType().equals("login")) {

            if (user != null) {

                BCrypt.Result result = BCrypt.verifyer().verify(loginInfo.getPassword().toCharArray(), user.getPasswordHash());
                if (result.verified) {

                    returnResponse(out, "login_success");
                    LogInUser(user, clientSocket);
                }
                else
                    returnResponse(out, "login_invalid");
            }
            else
                returnResponse(out, "login_notfound");
        }
        else if (loginInfo.getType().equals("register")) {

            if (user != null) {

                returnResponse(out, "register_success");
                LogInUser(user, clientSocket);
            }
            else
                returnResponse(out, "register_taken");
        }
        else
            LogAction("Invalid connection type specified!");
    }
}
