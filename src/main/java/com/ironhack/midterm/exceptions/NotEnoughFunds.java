package com.ironhack.midterm.exceptions;

public class NotEnoughFunds extends RuntimeException{

    public NotEnoughFunds(String message){
        super(message);
    }
}
