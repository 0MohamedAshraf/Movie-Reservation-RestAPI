package com.example.moviereservation.dto;


import java.time.LocalDateTime;
import java.util.Date;

public class ScheduleDto {
    private String id;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Date date;

    private Double price;

    private String movieId;

    private String theaterId;

    public ScheduleDto() {
    }

    public ScheduleDto(String id, LocalDateTime startTime, LocalDateTime endTime, Date date, Double price, String movieId, String theaterId) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.date = date;
        this.price = price;
        this.movieId = movieId;
        this.theaterId = theaterId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getTheaterId() {
        return theaterId;
    }

    public void setTheaterId(String theaterId) {
        this.theaterId = theaterId;
    }
}
