package com.example.moviereservation.dto;


public class TheaterDto {
    private String id;

    private String name;

    private String city;

    private Integer totalSeats;

    public TheaterDto() {
    }

    public TheaterDto(String id, String name, String city, Integer totalSeats) {
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
}
