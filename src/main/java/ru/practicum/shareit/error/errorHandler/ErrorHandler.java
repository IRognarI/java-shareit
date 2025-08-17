package ru.practicum.shareit.error.errorHandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.error.ErrorResponse;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.exception.itemException.ItemNotFoundException;
import ru.practicum.shareit.exception.userException.EmailDuplicatedException;
import ru.practicum.shareit.exception.userException.UserDuplicatedException;
import ru.practicum.shareit.exception.userException.UserNotFoundException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> userDuplicated(final UserDuplicatedException e) {
        log.debug(e.getMessage());

        ErrorResponse error = new ErrorResponse("Ошибка при регистрации пользователя", e.getMessage());

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> userFoundException(final UserNotFoundException e) {
        log.debug(e.getMessage());

        ErrorResponse error = new ErrorResponse("Ошибка поиска пользователя", e.getMessage());

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> nullException(final ValidationException e) {
        log.debug(e.getMessage());

        ErrorResponse error = new ErrorResponse("Ошибка валидации", e.getMessage());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> emailInvalid(final MethodArgumentNotValidException e) {
        log.debug(e.getMessage());

        ErrorResponse error = new ErrorResponse("Не корректный email адрес", e.getMessage());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> emailDuplicated(final EmailDuplicatedException e) {
        log.debug(e.getMessage());

        ErrorResponse error = new ErrorResponse("Проблема с email адресом", e.getMessage());

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> searchItemFailed(final ItemNotFoundException e) {
        log.debug(e.getMessage());

        ErrorResponse error = new ErrorResponse("Ошибка поиска вещи", e.getMessage());

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}
