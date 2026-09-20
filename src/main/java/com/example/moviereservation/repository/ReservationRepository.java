package com.example.moviereservation.repository;

import com.example.moviereservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation,Integer> {

    @Query("SELECT res from Reservation res " +
            "join res.user usr " +
            "WHERE usr.id = :userId " +
            "AND res.status IN (ReservationStatus.CONFIRMED,ReservationStatus.PENDING)")
    List<Reservation> findByUserId(Integer userId);
}
