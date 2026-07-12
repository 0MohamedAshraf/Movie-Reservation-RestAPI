package com.example.moviereservation.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "schedule_seat")
public class ScheduleSeat {
    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "schedule_id",nullable = false)
    private Schedule schedule;

    @ManyToOne
    @JoinColumn(name = "seat_id",nullable = false)
    private Seat seat;

    public ScheduleSeat() {
    }

    public ScheduleSeat(String id, Schedule schedule, Seat seat) {
        this.id = id;
        this.schedule = schedule;
        this.seat = seat;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }
}
