package com.example.moviereservation.dto.request;

import com.example.moviereservation.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto {
    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private String city;

    private UserRole role;
}
