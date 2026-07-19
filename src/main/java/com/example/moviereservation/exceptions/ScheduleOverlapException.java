package com.example.moviereservation.exceptions;

public class ScheduleOverlapException extends RuntimeException{
    public ScheduleOverlapException(String message) {
        super(message);
    }
}
