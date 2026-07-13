package com.example.moviereservation.repository;

import com.example.moviereservation.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie,String> {

    Movie findByTitle(String title);

    List<Movie> findByCategory(String category);

    @Query("SELECT m from Movie m order By m.rating DESC")
    List<Movie> findTopRated();
}
