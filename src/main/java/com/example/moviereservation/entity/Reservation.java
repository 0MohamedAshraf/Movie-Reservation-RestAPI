package com.example.moviereservation.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "total_price")
    private Float totalPrice;

    @Column(name = "booking_date")
    private Date bookingDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    public Reservation() {
    }

    public Reservation(Integer id, String paymentMethod, Float totalPrice, Date bookingDate, Schedule schedule) {
        this.id = id;
        this.paymentMethod = paymentMethod;
        this.totalPrice = totalPrice;
        this.bookingDate = bookingDate;
        this.schedule = schedule;
    }

}
