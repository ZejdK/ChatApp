package ba.unsa.etf.rpr;

import java.io.Serializable;

public class ChatInput implements Serializable {

    public String content;

    public ChatInput(String content) {
        this.content = content;
    }
}
