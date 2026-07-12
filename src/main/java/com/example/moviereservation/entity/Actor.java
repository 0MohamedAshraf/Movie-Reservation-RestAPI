package com.example.moviereservation.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table(name = "actor")
public class Actor {

    @Id
    private String id;

    private String name;

    private String bio;

    @ManyToMany(mappedBy = "actors")
    private List<Movie> movies;

    public Actor() {
    }

    public Actor(String id, String name, String bio) {
        this.id = id;
        this.name = name;
        this.bio = bio;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
