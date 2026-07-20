package com.example.moviereservation.entity;

import com.example.moviereservation.enums.SeatClass;
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
@Table(name = "seat")
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "row_num")
    private Character rowNum;

    @Column(name = "seat_num")
    private Integer seatNum;

    @Column(name = "class")
    @Enumerated(EnumType.STRING)
    private SeatClass seatClass;

    @ManyToOne
    @JoinColumn(name = "theater_id",nullable = false)
    private Theater theater;



}
