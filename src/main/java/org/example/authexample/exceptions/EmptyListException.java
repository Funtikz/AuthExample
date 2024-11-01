package org.example.authexample.exceptions;


public class EmptyListException extends RuntimeException {

    public EmptyListException(){
        super("This list is empty :(");
    }
}
