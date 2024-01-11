package ba.unsa.etf.rpr.chatapp.server.beans;

public class Role {

    private int id;
    private String name;
    private String color;
    private String description;

    private boolean canSendMessages;
    private boolean isOperator;
    private boolean canModerate;
    private boolean canSkipSlowdown;

    public Role(int id, String name, String color, String description) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.description = description;
        this.canSendMessages = true;
        this.isOperator = false;
        this.canModerate = false;
        this.canSkipSlowdown = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCanSendMessages() {
        return canSendMessages;
    }

    public void setCanSendMessages(boolean canSendMessages) {
        this.canSendMessages = canSendMessages;
    }

    public boolean isOperator() {
        return isOperator;
    }

    public void setOperator(boolean operator) {
        isOperator = operator;
    }

    public boolean isCanModerate() {
        return canModerate;
    }

    public void setCanModerate(boolean canModerate) {
        this.canModerate = canModerate;
    }

    public boolean isCanSkipSlowdown() {
        return canSkipSlowdown;
    }

    public void setCanSkipSlowdown(boolean canSkipSlowdown) {
        this.canSkipSlowdown = canSkipSlowdown;
    }
}
