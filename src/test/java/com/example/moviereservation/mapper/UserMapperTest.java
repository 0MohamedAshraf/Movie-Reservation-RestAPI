package com.example.moviereservation.mapper;

import com.example.moviereservation.dto.request.UserRequestDto;
import com.example.moviereservation.dto.response.UserResponseDto;
import com.example.moviereservation.entity.User;
import com.example.moviereservation.enums.UserRole;
import com.example.moviereservation.support.TestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserMapper")
class UserMapperTest {

    private final UserMapper mapper = new UserMapper();

    @Test
    @DisplayName("toEntity maps every field, including the password")
    void toEntity() {
        UserRequestDto request = TestData.userRequest("jane@example.com", "secret123");

        User user = mapper.toEntity(request);

        assertThat(user.getId()).isNull();
        assertThat(user.getFirstName()).isEqualTo("Jane");
        assertThat(user.getLastName()).isEqualTo("Doe");
        assertThat(user.getEmail()).isEqualTo("jane@example.com");
        assertThat(user.getPassword()).isEqualTo("secret123");
        assertThat(user.getCity()).isEqualTo("Cairo");
        assertThat(user.getRole()).isEqualTo(UserRole.CUSTOMER);
    }

    @Test
    @DisplayName("toDto maps every field but never exposes the password")
    void toDto() {
        User user = TestData.user(5, "jane@example.com", UserRole.ADMIN);

        UserResponseDto dto = mapper.toDto(user);

        assertThat(dto.getId()).isEqualTo(5);
        assertThat(dto.getFirstName()).isEqualTo("Jane");
        assertThat(dto.getLastName()).isEqualTo("Doe");
        assertThat(dto.getEmail()).isEqualTo("jane@example.com");
        assertThat(dto.getCity()).isEqualTo("Cairo");
        assertThat(dto.getRole()).isEqualTo(UserRole.ADMIN);
    }
}
