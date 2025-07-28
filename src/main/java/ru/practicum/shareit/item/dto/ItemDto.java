package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder(toBuilder = true)
@Setter(AccessLevel.NONE)
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long owner;
    private String request;
}
