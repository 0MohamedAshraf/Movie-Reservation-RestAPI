package com.example.moviereservation.repository;

import com.example.moviereservation.entity.ReservationSeat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReservationSeatRepository extends JpaRepository<ReservationSeat,Integer> {

    @Query(value = """
        SELECT ss.id
        FROM reservation_seat rs
        join schedule_seat ss
        ON rs.schedule_seat_id = ss.id
        WHERE reservation_id = :reservationId
""",nativeQuery = true)
    List<Integer> getReservedSeatsIds(Integer reservationId);

    boolean existsByReservation_Id(Integer reservationId);
    
    boolean existsByReservation_IdAndScheduleSeat_Id(Integer reservationId, Integer scheduleSeatId);
}
