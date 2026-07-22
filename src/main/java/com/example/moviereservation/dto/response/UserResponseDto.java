package com.example.moviereservation.dto.response;

import com.example.moviereservation.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private Integer id;

    private String firstName;

    private String lastName;

    private String email;

    private String city;

    private UserRole role;
}
