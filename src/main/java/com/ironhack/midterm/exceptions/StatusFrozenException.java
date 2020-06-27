package com.ironhack.midterm.exceptions;

public class StatusFrozenException extends RuntimeException {
    public StatusFrozenException(String message){
        super(message);
    }
}
