package ba.unsa.etf.rpr.chatappserver;

public class UserDisconnectedException extends Exception {

    public UserDisconnectedException(String errorMessage) {

        super(errorMessage);
    }
}
