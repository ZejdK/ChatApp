package ba.unsa.etf.rpr.chatapp.shared.dto;

import java.io.Serializable;

public record UserView(long id, String username, String roleList) implements Serializable {}
