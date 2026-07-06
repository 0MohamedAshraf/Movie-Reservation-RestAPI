package com.example.moviereservation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "theater")
public class Theater {

    @Id
    private String id;

    private String name;

    private String city;

    @Column(name = "total_seats")
    private Integer totalSeats;

    public Theater() {
    }

    public Theater(String id, String name, String city, Integer totalSeats) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.totalSeats = totalSeats;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(Integer totalSeats) {
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
