package com.example.moviereservation.exceptions;

public class InvalidMovieEntityException extends RuntimeException{
    public InvalidMovieEntityException(String message) {
        super(message);
    }
}
