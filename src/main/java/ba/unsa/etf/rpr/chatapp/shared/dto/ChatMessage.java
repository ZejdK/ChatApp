package ba.unsa.etf.rpr.chatapp.shared.dto;

import java.io.Serializable;

public class ChatMessage implements Serializable {

    public String content;
    public String author;
    // timestamp

    public ChatMessage(String author, String content) {
        this.content = content;
        this.author = author;
    }

    @Override
    public String toString() {

        return author + ": " + content;
    }
}
