package com.example.moviereservation.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "reservation_seat")
public class ReservationSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "reservation_id",nullable = false)
    private Reservation reservation;

    @ManyToOne
    @JoinColumn(name = "schedule_seat_id")
    private ScheduleSeat scheduleSeat;


    public ReservationSeat() {
    }

    public ReservationSeat(Integer id, Reservation reservation, ScheduleSeat scheduleSeat) {
        this.id = id;
        this.reservation = reservation;
        this.scheduleSeat = scheduleSeat;
    }

}
