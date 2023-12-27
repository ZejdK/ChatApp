package ba.unsa.etf.rpr.chatapp.business;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

public class ServerConnection {

    private final Socket connection;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;
    private Thread serverListener;
    private final ArrayList< Consumer<Object> > consumers;
    private boolean stopThread;

    public ServerConnection(String ip, int port) throws IOException {

        connection = new Socket(ip, port);
        out = new ObjectOutputStream(connection.getOutputStream());
        in = new ObjectInputStream(connection.getInputStream());
        consumers = new ArrayList<>();
        stopThread = false;
    }

    public void addConsumer(Consumer<Object> c) {

        consumers.add(c);
        System.out.println("there are now " + consumers.size() + " consumers");
    }

    public void startListener() {

        serverListener = new Thread(() -> {

            try {
                while (!stopThread) {
                    Object o = in.readObject();
                    consumers.forEach(c -> c.accept(o));
                }
            } catch (Exception e) { e.printStackTrace(); }
        });

        serverListener.start();
    }

    public void send(Object o) throws IOException {

        System.out.println("Sending to the server " + o.toString());
        out.writeObject(o);
        out.flush();
    }

    public void close() throws InterruptedException {

        stopThread = true;
        serverListener.join();
    }
}
