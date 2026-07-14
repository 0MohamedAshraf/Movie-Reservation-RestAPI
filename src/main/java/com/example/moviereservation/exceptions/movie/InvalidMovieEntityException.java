package com.example.moviereservation.exceptions.movie;

public class InvalidMovieEntityException extends RuntimeException{
    public InvalidMovieEntityException(String message) {
        super(message);
    }
}
