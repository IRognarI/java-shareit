package ru.practicum.shareit.booking.exception;

public class UserIsNotOwnerItemException extends RuntimeException {
    public UserIsNotOwnerItemException(String message) {
        super(message);
    }
}
