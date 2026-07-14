package com.example.moviereservation.exceptions.theater;

public class TheaterNotFoundException extends RuntimeException{
    public TheaterNotFoundException(String message) {
        super(message);
    }
}
