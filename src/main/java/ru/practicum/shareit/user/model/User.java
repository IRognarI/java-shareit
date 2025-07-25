package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(of = {"id", "email"})
public class User {
    private final Long id;

    @NonNull
    private String name;

    @NonNull
    @Email(message = "Укажите корректный email адрес")
    private String email;
}
