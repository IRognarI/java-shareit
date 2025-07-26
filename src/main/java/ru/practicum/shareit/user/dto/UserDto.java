package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import ru.practicum.shareit.user.model.User;

@Data
@Builder(toBuilder = true)
@Setter(AccessLevel.NONE)
public class UserDto {
    private Long id;

    @Email(message = "Укажите корректный email адрес")
    private String email;
    private String name;

    public static UserDto userToDto(User user) {

        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }
}
