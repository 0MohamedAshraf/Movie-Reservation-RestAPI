package com.example.moviereservation.entity;


import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Reservation {
    @Id
    private String id;

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

    public Reservation(String id, String paymentMethod, Float totalPrice, Date bookingDate, Schedule schedule) {
        this.id = id;
        this.paymentMethod = paymentMethod;
        this.totalPrice = totalPrice;
        this.bookingDate = bookingDate;
        this.schedule = schedule;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }
}
