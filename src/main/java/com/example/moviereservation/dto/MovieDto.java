package com.example.moviereservation.dto;

import java.time.LocalDate;
import java.util.List;

public class MovieDto {
    private String id;

    private String title;

    private String description;

    private Float rating;

    private LocalDate releaseDate;

    private String category;

    private Float duration;

    private List<ScheduleDto> schedules;

    public MovieDto() {
    }

    public MovieDto(String id, String title, String description, Float rating, LocalDate releaseDate, String category, Float duration, List<ScheduleDto> schedules) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.category = category;
        this.duration = duration;
        this.schedules = schedules;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Float getDuration() {
        return duration;
    }

    public void setDuration(Float duration) {
        this.duration = duration;
    }

    public List<ScheduleDto> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<ScheduleDto> schedules) {
        this.schedules = schedules;
    }
}
