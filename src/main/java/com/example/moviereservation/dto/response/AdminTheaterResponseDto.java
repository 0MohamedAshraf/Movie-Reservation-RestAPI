package com.example.moviereservation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminTheaterResponseDto {
    private Integer id;
    private String name;
    private String city;
    private Integer totalSeats;
}
