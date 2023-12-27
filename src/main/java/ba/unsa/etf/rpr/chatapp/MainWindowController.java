package ba.unsa.etf.rpr.chatapp;

import ba.unsa.etf.rpr.ChatInput;
import ba.unsa.etf.rpr.ChatMessage;
import ba.unsa.etf.rpr.chatapp.business.ServerConnection;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.input.KeyCode;

import java.util.ArrayList;

public class MainWindowController {

    public ListView<String> mainWindow_userListId;
    public ListView<String> mainWindow_channelListId;
    public ListView<String> mainWindow_chatLogId;
    public TextField mainWindow_chatInputId;

    private final ServerConnection serverConn;

    ArrayList<String> chatLog;
    ObservableList<String> chatLogList;

    public MainWindowController(ServerConnection serverConn) {

        this.serverConn = serverConn;
    }

    @FXML
    public void initialize() {

        try {

            chatLog = new ArrayList<>();
            chatLogList = FXCollections.observableArrayList("Welcome to the chatroom");

            mainWindow_chatLogId.setItems(chatLogList);
            mainWindow_chatLogId.setCellFactory(ComboBoxListCell.forListView(chatLogList));


            serverConn.addConsumer((Object o) -> {

                System.out.println("[MAIN] Received object of type " + o);
                if (o instanceof ChatMessage chatMessage) {

                    Platform.runLater(() -> {

                        chatLogList.add(chatMessage.toString());
                        mainWindow_chatLogId.scrollTo(chatLogList.size() - 1);
                    });
                }
            });

        } catch (Exception e) { e.printStackTrace(); }

        mainWindow_chatInputId.setOnKeyReleased(event -> {

            if (event.getCode() == KeyCode.ENTER){

                String msg = mainWindow_chatInputId.getText();
                if (msg.length() > 0) {
                    try {
                        serverConn.send(new ChatInput(msg));
                    } catch (Exception e) { e.printStackTrace(); }
                }

                mainWindow_chatInputId.clear();
            }
        });
    }

    @FXML
    public void shutdown() throws InterruptedException {

        System.out.println("Shutting down...");
        serverConn.close();
    }
}
