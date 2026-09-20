package com.example.moviereservation.repository;

import com.example.moviereservation.entity.ScheduleSeat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleSeatRepository extends JpaRepository<ScheduleSeat,Integer> {

    List<ScheduleSeat> getByScheduleId(Integer scheduleId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT s
        from ScheduleSeat s
        WHERE s.id in :ids
        
""")
    List<ScheduleSeat> getScheduleSeatsForUpdate(@Param("ids") List<Integer> ids);

    boolean existsByScheduleId(Integer scheduleId);

}
