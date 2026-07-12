package com.example.moviereservation.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "seat")
public class Seat {
    @Id
    private String id;

    @Column(name = "row_num")
    private Integer rowNum;

    @Column(name = "seat_num")
    private Integer seatNum;

    @Column(name = "class")
    private Character seatClass;

    @ManyToOne
    @JoinColumn(name = "theater_id",nullable = false)
    private Theater theater;


    public Seat() {
    }

    public Seat(String id, Integer rowNum, Integer seatNum, Character seatClass, Theater theater) {
        this.id = id;
        this.rowNum = rowNum;
        this.seatNum = seatNum;
        this.seatClass = seatClass;
        this.theater = theater;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getRowNum() {
        return rowNum;
    }

    public void setRowNum(Integer rowNum) {
        this.rowNum = rowNum;
    }

    public Integer getSeatNum() {
        return seatNum;
    }

    public void setSeatNum(Integer seatNum) {
        this.seatNum = seatNum;
    }

    public Character getSeatClass() {
        return seatClass;
    }

    public void setSeatClass(Character seatClass) {
        this.seatClass = seatClass;
    }

    public Theater getTheater() {
        return theater;
    }

    public void setTheater(Theater theater) {
        this.theater = theater;
    }
}
