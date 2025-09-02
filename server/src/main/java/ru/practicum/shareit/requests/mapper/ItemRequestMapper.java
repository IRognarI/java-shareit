package ru.practicum.shareit.requests.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.mapper.UserMapper;

@UtilityClass
public class ItemRequestMapper {

    private UserMapper mapper;

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                mapper.toUserDto(itemRequest.getRequestor()),
                itemRequest.getCreated(),
                null
        );
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        return new ItemRequest(
                null,
                itemRequestDto.getDescription(),
                null,
                null
        );
    }
}