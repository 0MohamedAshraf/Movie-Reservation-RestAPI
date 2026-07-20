package com.example.moviereservation.repository;

import com.example.moviereservation.entity.ReservationSeat;

import com.example.moviereservation.entity.ScheduleSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
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

    @Query(value = """
        SELECT ss.*
        FROM reservation_seat rs
        join schedule_seat ss
        ON rs.schedule_seat_id = ss.id
        WHERE reservation_id = :reservationId
""",nativeQuery = true)
    List<ScheduleSeat> getReservedSeats(Integer reservationId);

    List<ReservationSeat> getByReservation_Id(Integer reservationId);

    @Modifying
    @NativeQuery(value = """
    DELETE FROM reservation_seat WHERE reservation_id = :reservationId
""")
    void deleteAllByReservationId(Integer reservationId);

    boolean existsByReservation_Id(Integer reservationId);
    
    boolean existsByReservation_IdAndScheduleSeat_Id(Integer reservationId, Integer scheduleSeatId);
}
