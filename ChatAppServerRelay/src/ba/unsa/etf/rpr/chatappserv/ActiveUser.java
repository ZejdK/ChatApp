package ba.unsa.etf.rpr.chatappserv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

public class ActiveUser implements Runnable {

    private final Socket socket;
    private boolean isValid;
    private final PrintWriter out;
    private final BufferedReader in;
    private final Consumer<ChatMessage> msgListener;
    // private final Consumer<ChatMessage> joinListener;
    private final Consumer<ChatMessage> leaveListener;
    private String username;

    ActiveUser(Socket clientSocket, String username, Consumer<ChatMessage> msgListener, Consumer<ChatMessage> joinListener, Consumer<ChatMessage> leaveListener) throws IOException {

        isValid = true;
        this.username = username;

        this.msgListener = msgListener;
        // this.joinListener = joinListener;
        this.leaveListener = leaveListener;

        socket = clientSocket;
        out = new PrintWriter(socket.getOutputStream());
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        joinListener.accept(new ChatMessage(username, "has joined the chat"));
    }

    public void sendMessage(String msg) {

        out.println(msg);
        out.flush();
    }

    @Override
    public void run() {

        try {
            while (true) {

                String msg = in.readLine();
                if (msg == null)
                    break;

                if (msgListener != null)
                    msgListener.accept(new ChatMessage(username, msg));
            }

            in.close();
            out.close();
            socket.close();
            leaveListener.accept(new ChatMessage(username, "has left the chat"));

        } catch(Exception e) { e.printStackTrace(); }

        isValid = false;
    }

    public void getInfo() {

        System.out.println(socket.getLocalSocketAddress());
        System.out.println(socket.getInetAddress());
        System.out.println(socket.getPort());
        System.out.println(socket.isBound());
        System.out.println(socket.isClosed());
        System.out.println(socket.isInputShutdown());
        System.out.println(socket.isOutputShutdown());
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
