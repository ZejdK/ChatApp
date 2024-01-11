package ba.unsa.etf.rpr.chatapp.server.exceptions;

public class UserNotFoundException extends Exception {

    public UserNotFoundException(String errorMessage) {

        super(errorMessage);
    }
}
