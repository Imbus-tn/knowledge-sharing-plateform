package com.imbus.knowledge.User_Management.exception;

public class OtpExpiredException extends RuntimeException{
    public OtpExpiredException(String message){
        super(message);
    }
}
