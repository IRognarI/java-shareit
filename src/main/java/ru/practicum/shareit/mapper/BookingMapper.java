package ru.practicum.shareit.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.model.Booking;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking, ItemDto itemDto, UserDto bookerDto) {
        if (booking == null) {
            return null;
        }
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                itemDto,
                bookerDto,
                booking.getStatus()
        );
    }

    public static BookingShortDto toBookingShortDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        return new BookingShortDto(
                booking.getId(),
                booking.getBooker().getId(),
                booking.getStart(),
                booking.getEnd()
        );
    }

    public static Booking toBooking(BookingInputDto bookingInputDto, Item item, User booker) {
        return new Booking(
                null,
                bookingInputDto.getStart(),
                bookingInputDto.getEnd(),
                item,
                booker,
                Status.WAITING
        );
    }
}