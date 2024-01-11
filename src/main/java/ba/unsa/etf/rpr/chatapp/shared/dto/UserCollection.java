package ba.unsa.etf.rpr.chatapp.shared.dto;

import java.io.Serializable;
import java.util.List;

public record UserCollection(List<UserView> users) implements Serializable {}
