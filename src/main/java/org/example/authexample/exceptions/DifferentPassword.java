package org.example.authexample.exceptions;

public class DifferentPassword extends RuntimeException{
    public DifferentPassword(){
        super("Пароли не совпадают");
    }
}
