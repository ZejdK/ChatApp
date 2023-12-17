package ba.unsa.etf.rpr.chatapp;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.input.KeyCode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class MainWindowController {

    public ListView<String> mainWindow_userListId;
    public ListView<String> mainWindow_channelListId;
    public ListView<String> mainWindow_chatLogId;
    public TextField mainWindow_chatInputId;

    Socket clientSocket;
    BufferedReader in;
    PrintWriter out;

    ArrayList<String> chatLog;
    ObservableList<String> chatLogList;
    Thread messageListener;

    public MainWindowController(Socket clientSocket, PrintWriter out, BufferedReader in) {

        this.clientSocket = clientSocket;
        this.out = out;
        this.in = in;
    }

    @FXML
    public void initialize() {

        try {

            // todo: opcija za pamcenje konekcija do sada i za racune, takodjer "Remember me" checkbox
            // todo: poslati informacije poput username, pw i ostalo tokom prijave
            // todo: IP adresa i port trebaju se citati iz .json config fajla
            // todo: ne zaboraviti pravilno zavrsiti threadove

            chatLog = new ArrayList<>();
            chatLogList = FXCollections.observableArrayList("Welcome to the chatroom");

            mainWindow_chatLogId.setItems(chatLogList);
            mainWindow_chatLogId.setCellFactory(ComboBoxListCell.forListView(chatLogList));

            messageListener = new Thread(() -> {

                try {
                    try {
                        while (true) {

                            String msg = in.readLine();
                            if (msg == null)
                                break;

                            System.out.println("Server response: " + msg);
                            Platform.runLater(() -> {

                                chatLogList.add(msg);
                                mainWindow_chatLogId.scrollTo(chatLogList.size() - 1);
                            });
                        }
                    } catch (Exception e) { e.printStackTrace(); }

                    out.close();
                    clientSocket.close();
                    System.out.println("Connection closed");

                } catch (IOException e) { e.printStackTrace(); }
            });

            messageListener.start();

        } catch (Exception e) { e.printStackTrace(); }

        mainWindow_chatInputId.setOnKeyReleased(event -> {

            if (event.getCode() == KeyCode.ENTER){

                String msg = mainWindow_chatInputId.getText();
                if (msg.length() > 0) {

                    try {

                        System.out.println("Sending: " + msg);
                        out.println(msg);
                        out.flush();

                    } catch (Exception e) { e.printStackTrace(); }
                }

                mainWindow_chatInputId.clear();
            }
        });
    }

    @FXML
    protected void shutdown() throws IOException {

        System.out.println("Stopping...");
        clientSocket.shutdownInput();
        System.out.println("stopped");
        //messageListener.join();
    }
}
