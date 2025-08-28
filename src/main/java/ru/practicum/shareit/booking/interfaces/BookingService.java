package ru.practicum.shareit.booking.interfaces;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.model.Booking;

import java.util.List;

/**
 * Объявлены CRUD методы и методы получения сущностей с заданными параметрами
 *
 */

public interface BookingService {
    BookingDto create(BookingInputDto bookingDto, Long bookerId);

    BookingDto update(Long bookingId, Long userId, Boolean approved);

    BookingDto getBookingById(Long bookingId, Long userId);

    List<BookingDto> getBookings(String state, Long userId);

    List<BookingDto> getBookingsOwner(String state, Long userId);

    BookingShortDto getLastBooking(Long itemId);

    BookingShortDto getNextBooking(Long itemId);

    Booking getBookingWithUserBookedItem(Long itemId, Long userId);

}
