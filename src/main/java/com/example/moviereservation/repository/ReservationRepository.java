package com.example.moviereservation.repository;

import com.example.moviereservation.entity.Reservation;
import com.example.moviereservation.enums.ReservationStatus;
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
    List<Reservation> findActiveByUserId(Integer userId);

    @Query("""
            SELECT DISTINCT r FROM Reservation r
            LEFT JOIN FETCH r.user
            JOIN FETCH r.schedule s
            JOIN FETCH s.movie
            JOIN FETCH s.theater
            ORDER BY r.id DESC
            """)
    List<Reservation> findAllForAdmin();

    long countByStatus(ReservationStatus status);

    @Query("SELECT res from Reservation res " +
            "WHERE res.user.id = :userId")
    List<Reservation> findAllByUserId(Integer userId);
}
