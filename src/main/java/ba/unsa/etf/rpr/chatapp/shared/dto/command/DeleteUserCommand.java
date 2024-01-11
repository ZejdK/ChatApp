package ba.unsa.etf.rpr.chatapp.shared.dto.command;

import java.io.Serializable;

public record DeleteUserCommand(long id) implements Command, Serializable {}
