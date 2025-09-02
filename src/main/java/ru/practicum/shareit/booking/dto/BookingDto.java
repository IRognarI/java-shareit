package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

/**
 * DTO объект
 * Используется для передачи информации об объекте Booking "наружу"
 * Несет в себе информацию об id бронирования, дате начала брони и об
 * ее окончании, владельце вещи которую арендуют, о самом арендаторе
 * и об статусе брони
 */

@Data
@AllArgsConstructor
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemDto item;
    private UserDto booker;
    private Status status;
}
