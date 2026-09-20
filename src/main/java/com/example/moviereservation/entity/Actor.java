package com.example.moviereservation.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "actor")
public class Actor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String bio;

    @ManyToMany(mappedBy = "actors")
    private List<Movie> movies;

    public Actor() {
    }

    public Actor(Integer id, String name, String bio) {
        this.id = id;
        this.name = name;
        this.bio = bio;
    }

}
