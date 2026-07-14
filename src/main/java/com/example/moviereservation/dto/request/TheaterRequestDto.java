package com.example.moviereservation.dto.request;

import com.example.moviereservation.dto.response.ScheduleResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TheaterRequestDto {

    private String name;

    private String city;

    private Integer totalSeats;

}
