package com.example.moviereservation.repository;

import com.example.moviereservation.entity.Theater;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TheaterRepository extends JpaRepository<Theater,Integer> {

    Theater findByName(String name);

    List<Theater> findByCity(String city);

    boolean existsByNameAndCity(String name, String city);
}
