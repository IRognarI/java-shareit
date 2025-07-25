package ru.practicum.shareit.user.mapper;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Data
@Builder(toBuilder = true)
public class MapToUser {

    public static User mapToUser(UserDto userDto) {

        return User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }
}
