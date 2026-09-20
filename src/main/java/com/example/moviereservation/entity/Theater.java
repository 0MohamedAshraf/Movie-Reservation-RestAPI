package com.example.moviereservation.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Setter
@Getter
@Entity
@Table(name = "theater")
public class Theater {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String city;

    @Column(name = "total_seats")
    private Integer totalSeats;

    @OneToMany(mappedBy = "theater")
    private List<Schedule> scheduleList;

    public Theater() {
    }

    public Theater(Integer id, String name, String city, Integer totalSeats) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.totalSeats = totalSeats;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Theater theater = (Theater) o;
        return Objects.equals(getId(), theater.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
