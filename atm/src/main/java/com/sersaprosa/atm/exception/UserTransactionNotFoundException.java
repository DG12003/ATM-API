package com.sersaprosa.atm.exception;

public class UserTransactionNotFoundException extends RuntimeException{
    public UserTransactionNotFoundException(String message) {
        super(message);
    }
}
