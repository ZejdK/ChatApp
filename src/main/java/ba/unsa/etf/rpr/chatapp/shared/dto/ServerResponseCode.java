package ba.unsa.etf.rpr.chatapp.shared.dto;

public enum ServerResponseCode {

    LOGIN_SUCCESS,
    LOGIN_INVALIDPASSWORD,
    LOGIN_USERNAMENOTFOUND,

    REGISTER_SUCCESS,
    REGISTER_USERNAMETAKEN,
    REGISTER_UNKNOWNFAIL
}
