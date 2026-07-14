package com.example.moviereservation.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TheaterResponseDto {
    private Integer id;

    private String name;

    private String city;

    private Integer totalSeats;

}
