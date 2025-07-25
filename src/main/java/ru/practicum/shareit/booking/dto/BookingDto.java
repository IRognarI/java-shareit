package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.booking.Booking;

/**
 * TODO Sprint add-bookings.
 */
@Data
@EqualsAndHashCode(of = {"id"})
@Builder(toBuilder = true)
public class BookingDto {
    private Long id;
    private LocalDateTime bookingStart;
    private LocalDateTime bookingEnd;
    private Long itemId;

    @Builder.Default
    private Set<Long> booker = new HashSet<>();

    public static BookingDto bookingToDto(Booking booking) {

        return BookingDto.builder()
                .id(booking.getId())
                .bookingStart(booking.getBookingStart())
                .bookingEnd(booking.getBookingEnd())
                .itemId(booking.getItemId())
                .booker(Set.copyOf(booking.getBooker()))
                .build();
    }
}
