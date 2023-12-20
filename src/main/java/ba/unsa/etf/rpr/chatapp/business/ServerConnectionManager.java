package ba.unsa.etf.rpr.chatapp.business;

import ba.unsa.etf.rpr.chatapp.ClientConfigDao;

import java.io.*;
import java.net.Socket;

public class ServerConnectionManager {

    private final Socket connection;
    private final PrintWriter out;
    private final BufferedReader in;

    public ServerConnectionManager() throws IOException {

        connection = new Socket(ClientConfigDao.getServerUrl(), ClientConfigDao.getServerPort());
        out = new PrintWriter(connection.getOutputStream());
        in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    }


    public String sendText(String text) {

        try {

            System.out.println("Sending: " + text);
            out.println(text);
            out.flush();

        } catch (Exception e) { e.printStackTrace(); System.out.println("Failed to send the message to server!"); }

        String serverResponse = null;
        try {

            System.out.println("Awaiting server response...");
            serverResponse = in.readLine();
            System.out.println("Server responsed with " + serverResponse);

        } catch (Exception e) { e.printStackTrace(); System.out.println("Failed to receive response from the server!"); }

        return serverResponse;
    }

    public String waitForText() throws IOException {

        String s = in.readLine();
        System.out.println("Server response: " + s);
        return s;
    }


    public void close() throws IOException {

        connection.close();
        out.close();
        in.close();
    }
}