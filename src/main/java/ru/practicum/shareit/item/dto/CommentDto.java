package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;

/*
 * DTO объект вместе с комментариями
 * Данный DTO объект передается "наружу"
 * Вместе с id комментария и самим содержанием,
 * передает информацию о владельце вещи и авторе комментария,
 * а так же дата публикации комментария.
 *
 *
 */

@Data
@AllArgsConstructor
public class CommentDto {
    private Long id;
    @NotEmpty
    @NotBlank
    private String text;
    @JsonIgnore
    private Item item;
    private String authorName;
    private LocalDateTime created;
}
