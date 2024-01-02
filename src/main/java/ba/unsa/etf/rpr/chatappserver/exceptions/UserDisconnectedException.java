package ba.unsa.etf.rpr.chatappserver.exceptions;

public class UserDisconnectedException extends Exception {

    public UserDisconnectedException(String errorMessage) {

        super(errorMessage);
    }
}
