package ba.unsa.etf.rpr.chatappserver;

import ba.unsa.etf.rpr.chatappserver.business.ServerLoop;

public class ChatAppServer {

    public static void main(String[] args) {

        try {
            ServerLoop s = new ServerLoop();
            s.startServer(30120);
        } catch (Exception e) { e.printStackTrace(); }
    }
}
