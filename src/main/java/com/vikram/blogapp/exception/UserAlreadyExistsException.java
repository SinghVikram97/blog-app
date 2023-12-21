package com.vikram.blogapp.exception;

public class UserAlreadyExistsException extends RuntimeException{
    String username;

    public UserAlreadyExistsException(String username) {
        super("User already exists with the username: "+username);
        this.username = username;
    }
}
