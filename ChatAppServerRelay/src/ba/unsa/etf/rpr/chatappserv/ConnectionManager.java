package ba.unsa.etf.rpr.chatappserv;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConnectionManager {

    // todo vidjeti da li ovaj socket salje bez ikakve zastite? https://docs.oracle.com/javase/8/docs/api/javax/net/ssl/SSLSocket.html
    private ServerSocket serverSocket;
    private ArrayList<ActiveUser> clients;
    ExecutorService ex;

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

        int counter = 0;

        while (true) {

            Socket c = serverSocket.accept(); // zaustavlja thread dok se client ne konektuje

            // todo: provjeriti ovo rjesenje za prosljedjivanje funkcija
            clients.add(new ActiveUser(c, "user" + counter, this::onMessageSent, this::broadcast, this::broadcast));

            ex.execute(clients.get(clients.size() - 1));
            ++counter;
        }
    }
}
