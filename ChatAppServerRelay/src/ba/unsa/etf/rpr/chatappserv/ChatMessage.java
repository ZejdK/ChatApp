package ba.unsa.etf.rpr.chatappserv;

import java.io.Serializable;

public class ChatMessage implements Serializable {

    public String username;
    public String content;
    // avatar URL
    // formatting / templateid
    // itd itd

    ChatMessage(String username, String content) {

        this.username = username;
        this.content = content;
    }
}
