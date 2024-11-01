package org.example.authexample.exceptions;

public class InsufficientBalance extends RuntimeException {
    public InsufficientBalance(){
        super("Недостаточный баланс");
    }
}
