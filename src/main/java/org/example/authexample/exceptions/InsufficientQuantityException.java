package org.example.authexample.exceptions;

public class InsufficientQuantityException extends RuntimeException {
    public InsufficientQuantityException(String s){
        super(s);
    }
}
