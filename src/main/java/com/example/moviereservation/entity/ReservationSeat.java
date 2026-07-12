package com.example.moviereservation.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "reservation_seat")
public class ReservationSeat {

    @Id
    @Column(name = "id", nullable = false, length = 100)
    private String id;

    @ManyToOne
    @JoinColumn(name = "reservation_id",nullable = false)
    private Reservation reservation;

    @ManyToOne
    @JoinColumn(name = "schedule_seat_id")
    private ScheduleSeat scheduleSeat;


    public ReservationSeat() {
    }

    public ReservationSeat(String id, Reservation reservation, ScheduleSeat scheduleSeat) {
        this.id = id;
        this.reservation = reservation;
        this.scheduleSeat = scheduleSeat;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public ScheduleSeat getScheduleSeat() {
        return scheduleSeat;
    }

    public void setScheduleSeat(ScheduleSeat scheduleSeat) {
        this.scheduleSeat = scheduleSeat;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
