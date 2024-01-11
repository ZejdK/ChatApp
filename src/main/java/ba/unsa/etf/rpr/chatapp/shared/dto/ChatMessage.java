package ba.unsa.etf.rpr.chatapp.shared.dto;

import java.io.Serializable;

public record ChatMessage(String author, String content) implements Serializable {}
