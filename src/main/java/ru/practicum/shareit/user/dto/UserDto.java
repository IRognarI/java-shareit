package ru.practicum.shareit.user.dto;

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
    private String name;
    private String email;

    public static UserDto userToDto(User user) {

        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
