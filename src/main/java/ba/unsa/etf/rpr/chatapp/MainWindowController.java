package ba.unsa.etf.rpr.chatapp;

import ba.unsa.etf.rpr.chatapp.business.ServerConnectionManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.input.KeyCode;

import java.io.IOException;
import java.util.ArrayList;

public class MainWindowController {

    public ListView<String> mainWindow_userListId;
    public ListView<String> mainWindow_channelListId;
    public ListView<String> mainWindow_chatLogId;
    public TextField mainWindow_chatInputId;

    private final ServerConnectionManager serverConn;

    ArrayList<String> chatLog;
    ObservableList<String> chatLogList;
    Thread messageListener;

    public MainWindowController(ServerConnectionManager serverConn) {

        this.serverConn = serverConn;
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
                    while (true) {

                        String msg = serverConn.waitForText();
                        if (msg == null)
                            break;

                        Platform.runLater(() -> {

                            chatLogList.add(msg);
                            mainWindow_chatLogId.scrollTo(chatLogList.size() - 1);
                        });
                    }

                } catch (Exception e) { e.printStackTrace(); }
            });

            messageListener.start();

        } catch (Exception e) { e.printStackTrace(); }

        mainWindow_chatInputId.setOnKeyReleased(event -> {

            if (event.getCode() == KeyCode.ENTER){

                String msg = mainWindow_chatInputId.getText();
                if (msg.length() > 0) {
                    try {
                        serverConn.sendText(msg);
                    } catch (Exception e) { e.printStackTrace(); }
                }

                mainWindow_chatInputId.clear();
            }
        });
    }

    @FXML
    protected void shutdown() {

        System.out.println("Stopping...");
        //messageListener.join();
        System.out.println("stopped");
    }
}
