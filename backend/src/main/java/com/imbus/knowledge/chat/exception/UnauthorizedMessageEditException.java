package com.imbus.knowledge.chat.exception;

public class UnauthorizedMessageEditException extends RuntimeException {
    public UnauthorizedMessageEditException() {
        super("User can only edit their own messages");
    }
}
