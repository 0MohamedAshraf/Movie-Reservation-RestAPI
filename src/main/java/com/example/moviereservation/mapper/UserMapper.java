package com.example.moviereservation.mapper;

import com.example.moviereservation.dto.request.UserRequestDto;
import com.example.moviereservation.dto.response.UserResponseDto;
import com.example.moviereservation.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserRequestDto request){
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setCity(request.getCity());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());
        user.setPassword(request.getPassword());
        user.setLastName(request.getLastName());
        return user;
    }

    public UserResponseDto toDto(User user){
        UserResponseDto dto = new UserResponseDto();

        dto.setId(user.getId());
        dto.setCity(user.getCity());
        dto.setRole(user.getRole());
        dto.setEmail(user.getEmail());
        dto.setLastName(user.getLastName());
        dto.setFirstName(user.getFirstName());

        return dto;
    }
}
