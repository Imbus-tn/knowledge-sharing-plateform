package com.imbus.knowledge.chat.exception;

public class ReactionNotFoundException extends RuntimeException {
    public ReactionNotFoundException() {
        super("Reaction not found");
    }
}