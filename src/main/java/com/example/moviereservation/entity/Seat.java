package com.example.moviereservation.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "seat")
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

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

    public Seat(Integer id, Integer rowNum, Integer seatNum, Character seatClass, Theater theater) {
        this.id = id;
        this.rowNum = rowNum;
        this.seatNum = seatNum;
        this.seatClass = seatClass;
        this.theater = theater;
    }

}
