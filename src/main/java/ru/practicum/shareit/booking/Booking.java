package ru.practicum.shareit.booking;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * TODO Sprint add-bookings.
 */
@Data
@EqualsAndHashCode(of = {"id"})
public class Booking {
    private Long id;
    private LocalDateTime bookingStart;
    private LocalDateTime bookingEnd;
    private Long itemId;
    private Set<Long> booker = new HashSet<>();

}
