package com.example.moviereservation.entity;


import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;


@Entity
@Table(name = "movie")
public class Movie {
    @Id
    private String id;

    private String title;

    private String description;

    private Float rating;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    private String category;

    private Float duration;

    @OneToMany(mappedBy = "movie")
    private List<Schedule> schedules;


    @ManyToMany
    @JoinTable(
            name = "acts",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "actor_id")
    )
    private List<Actor> actors;

    public Movie() {
    }

    public Movie(String id, String title, String description, Float rating, String category, Float duration) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.rating = rating;
        this.releaseDate = LocalDate.now();
        this.category = category;
        this.duration = duration;
    }

    public List<Actor> getActors() {
        return actors;
    }

    public void setActors(List<Actor> actors) {
        this.actors = actors;
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

    public void setReleaseDate(LocalDate release_date) {
        this.releaseDate = release_date;
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

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
    }
}
