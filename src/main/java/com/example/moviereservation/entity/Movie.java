package com.example.moviereservation.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;


@Setter
@Getter
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

}
