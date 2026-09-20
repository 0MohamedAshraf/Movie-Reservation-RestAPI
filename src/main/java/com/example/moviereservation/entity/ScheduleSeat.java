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
@Table(name = "schedule_seat")
public class ScheduleSeat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Boolean available;

    @ManyToOne
    @JoinColumn(name = "schedule_id",nullable = false)
    private Schedule schedule;

    @ManyToOne
    @JoinColumn(name = "seat_id",nullable = false)
    private Seat seat;


}
