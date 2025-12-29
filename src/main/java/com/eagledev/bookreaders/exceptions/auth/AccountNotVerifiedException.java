package com.eagledev.bookreaders.exceptions.auth;

public class AccountNotVerifiedException extends RuntimeException{
    public AccountNotVerifiedException(String message) {
        super(message);
    }
}
