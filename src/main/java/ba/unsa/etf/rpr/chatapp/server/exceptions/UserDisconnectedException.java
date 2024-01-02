package ba.unsa.etf.rpr.chatapp.server.exceptions;

public class UserDisconnectedException extends Exception {

    public UserDisconnectedException(String errorMessage) {

        super(errorMessage);
    }
}
