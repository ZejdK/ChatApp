package ba.unsa.etf.rpr.chatapp.shared.dto.command;

import java.io.Serializable;

public record EditUserCommand(Long id, String newUsername, String newRoleList) implements Command, Serializable {}
