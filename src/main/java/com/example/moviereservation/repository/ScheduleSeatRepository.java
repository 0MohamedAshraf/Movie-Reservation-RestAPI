package com.example.moviereservation.repository;

import com.example.moviereservation.entity.ScheduleSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleSeatRepository extends JpaRepository<ScheduleSeat,String> {

}
