package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(of = {"id", "email"})
public class User {
    private final Long id;

    @NotNull
    @NotBlank(message = "Имя не может быть пустым")
    private String name;

    @NotNull
    @Email(message = "Укажите корректный email адрес")
    @NotBlank(message = "Email адрес не может быть пустым")
    private String email;
}
