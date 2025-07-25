package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(of = {"id", "owner", "request"})
public class Item {
    private final Long id;

    @NonNull
    private String name;

    @NonNull
    private String description;

    @NonNull
    private Boolean available;

    @NonNull
    private final Long owner;

    private String request;
}
