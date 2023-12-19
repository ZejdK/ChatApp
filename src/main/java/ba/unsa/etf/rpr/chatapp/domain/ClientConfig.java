package ba.unsa.etf.rpr.chatapp.domain;

import java.io.Serializable;

public class ClientConfig implements Serializable {

    private String serverUrl;
    private int serverPort;

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }
}
