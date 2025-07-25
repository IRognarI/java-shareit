package ru.practicum.shareit.exception.userException;

public class EmailDuplicatedException extends RuntimeException {
    public EmailDuplicatedException(String message) {
        super(message);
    }
}
