package com.example.moviereservation.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
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




}
