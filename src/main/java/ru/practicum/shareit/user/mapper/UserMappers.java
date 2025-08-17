package ru.practicum.shareit.user.mapper;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Data
@Builder(toBuilder = true)
@UtilityClass
public class UserMappers {

    public static User mapToUser(UserDto userDto) {

        return User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    public static UserDto userToDto(User user) {

        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }
}
