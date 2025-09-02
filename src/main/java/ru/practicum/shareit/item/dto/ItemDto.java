package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

/*
 * DTO объект
 *  Данный объект передает данные "наружу"
 * Содержит в себе информацию:
 * id вещи, ее название, описание, статус доступности для брони,
 * информация о владельце этой вещи, ссылка - запрос на бронь,
 * дата последнего бронирования и ближайшего следующего,
 * а также комментарии к данной вещи.
 */

@Data
@AllArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    @JsonIgnore
    private User owner;
    private Long requestId;
    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;
    private List<CommentDto> comments;
}
