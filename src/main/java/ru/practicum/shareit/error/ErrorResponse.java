package ru.practicum.shareit.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ErrorResponse {
    private String message;
}
