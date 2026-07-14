package com.example.moviereservation.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "schedule_seat")
public class ScheduleSeat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "schedule_id",nullable = false)
    private Schedule schedule;

    @ManyToOne
    @JoinColumn(name = "seat_id",nullable = false)
    private Seat seat;

    public ScheduleSeat() {
    }

    public ScheduleSeat(Integer id, Schedule schedule, Seat seat) {
        this.id = id;
        this.schedule = schedule;
        this.seat = seat;
    }

}
