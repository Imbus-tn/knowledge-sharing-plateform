package com.imbus.knowledge.chat.exception;



public class UnauthorizedChatAccessException extends RuntimeException {
    public UnauthorizedChatAccessException() {
        super("User is not authorized to access this chat");
    }
}
