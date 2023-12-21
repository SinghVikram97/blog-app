package com.vikram.blogapp.exception;

public class InvalidAuthHeaderException extends RuntimeException{
    public String authHeader;

    public InvalidAuthHeaderException(String authHeader){
        super("Missing or Invalid Auth Header: "+authHeader);
        this.authHeader=authHeader;
    }
}
