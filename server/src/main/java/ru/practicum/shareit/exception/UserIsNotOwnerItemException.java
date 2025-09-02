package ru.practicum.shareit.exception;

public class UserIsNotOwnerItemException extends RuntimeException {
    public UserIsNotOwnerItemException(String message) {
        super(message);
    }
}
