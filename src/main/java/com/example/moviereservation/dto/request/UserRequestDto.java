package com.example.moviereservation.dto.request;

import com.example.moviereservation.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto {
    @NotNull
    @NotBlank
    private String firstName;

    @NotNull
    @NotBlank
    private String lastName;

    @NotNull
    @NotBlank
    @Email
    private String email;

    @NotBlank
    @NotNull
    @Size(min = 0,max = 64)
    private String password;

    @NotNull
    @NotBlank
    private String city;

    @NotNull
    private UserRole role;
}
