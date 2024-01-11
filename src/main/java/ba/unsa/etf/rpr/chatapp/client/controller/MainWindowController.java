package ba.unsa.etf.rpr.chatapp.client.controller;

import ba.unsa.etf.rpr.chatapp.client.ChatAppClient;
import ba.unsa.etf.rpr.chatapp.client.business.ServerConnection;
import ba.unsa.etf.rpr.chatapp.client.model.AdminModel;
import ba.unsa.etf.rpr.chatapp.shared.dto.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class MainWindowController {

    public ListView<String> mainWindow_userListId;
    public ListView<String> mainWindow_channelListId;
    public ListView<String> mainWindow_chatLogId;
    public TextField mainWindow_chatInputId;
    public Label statusBarLabel;

    private final ServerConnection serverConn;

    private ObservableList<String> chatLogList;

    // TODO: MainWindowModel

    public MainWindowController(ServerConnection serverConn) {

        this.serverConn = serverConn;
    }

    @FXML
    public void initialize() {

        try {

            chatLogList = FXCollections.observableArrayList("Welcome to the chatroom");

            mainWindow_chatLogId.setItems(chatLogList);
            mainWindow_chatLogId.setCellFactory(ComboBoxListCell.forListView(chatLogList));

            serverConn.addConsumer((Object o) -> {

                System.out.println("[MAIN] Received object of type " + o);
                if (o instanceof ChatMessage chatMessage) {

                    Platform.runLater(() -> {

                        chatLogList.add(formatChatMessage(chatMessage));
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

    public void menubarAdminPanelAction(ActionEvent actionEvent) throws IOException {

        AdminModel adminModel = new AdminModel();

        FXMLLoader loader = new FXMLLoader(ChatAppClient.class.getResource("model/adminWindow.fxml"));
        AdminWindowController adminWindowController = new AdminWindowController(adminModel);
        loader.setController(adminWindowController);

        Stage stage = new Stage();
        stage.setTitle("Admin panel");
        stage.setScene(new Scene(loader.load(), USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
        stage.setResizable(false);


        serverConn.addConsumer((Object o) -> {

            if (o instanceof ServerResponseCode serverResponseCode && serverResponseCode == ServerResponseCode.COLLECTION_REQUEST_FAILED) {

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Server error");
                alert.setHeaderText("Unable to fetch data from the server");
                alert.setContentText("");
                alert.showAndWait();
                updateStatusLabel("Idle");
            }
            else if (o instanceof UserCollection users) {

                adminWindowController.setData(users);
                updateStatusLabel("Successfully received response!");
            }
        });


        updateStatusLabel("Sending request to the server...");
        serverConn.send(ServerRequest.ADMINREQUEST_GETUSERS);
        updateStatusLabel("Waiting for the server response...");


        stage.setOnHidden((e) -> {

            // TODO: serverConn.removeConsumer(); // the one that was added above
        });

        stage.show();
    }

    private String formatChatMessage(ChatMessage chatMessage) {

        return chatMessage.author() + ": " + chatMessage.content();
    }

    private void updateStatusLabel(String text) {

        Platform.runLater(() -> statusBarLabel.setText(text));
    }
}
