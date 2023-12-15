package ba.unsa.etf.rpr.chatappserv;

public class Main {

    public static void main(String[] args) {

        int configurationPort = 30120;
        // todo: citati konfiguraciju iz fajla

        try {

            ConnectionManager cm = new ConnectionManager();
            cm.startServer(configurationPort);

        } catch (Exception e) { e.printStackTrace(); }
    }
}
