package ru.practicum.shareit.booking.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ItemNotFoundException extends IllegalArgumentException {
    public ItemNotFoundException(String message) {
        super(message);
        log.error(message);
    }
}
