package ba.unsa.etf.rpr.chatapp.shared.dto.command;

import java.io.Serializable;

public record AddUserCommand(String username, String password, String roleList) implements Command, Serializable {}
