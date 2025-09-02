package ru.practicum.shareit.booking.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingInputDtoTest {
    private JacksonTester<BookingInputDto> json;
    private BookingInputDto bookingInputDto;
    private Validator validator;

    public BookingInputDtoTest(@Autowired JacksonTester<BookingInputDto> json) {
        this.json = json;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void beforeEach() {
        bookingInputDto = new BookingInputDto(
                1L,
                LocalDateTime.of(2030, 12, 25, 12, 00),
                LocalDateTime.of(2030, 12, 26, 12, 00)
        );
    }

    @Test
    void testJsonBookingInputDto() throws Exception {
        JsonContent<BookingInputDto> result = json.write(bookingInputDto);
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2030-12-25T12:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2030-12-26T12:00:00");
    }

    @Test
    void whenBookingInputDtoIsValidThenViolationsShouldBeEmpty() {
        Set<ConstraintViolation<BookingInputDto>> violations = validator.validate(bookingInputDto);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenBookingInputDtoItemIdNotNullThenViolationsShouldBeReportedNotNull() {
        bookingInputDto.setItemId(null);

        Set<ConstraintViolation<BookingInputDto>> violations = validator.validate(bookingInputDto);


        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("itemId");
    }

    @Test
    void whenBookingInputDtoStartIsNullThenViolationsShouldBeReported() {
        bookingInputDto.setStart(null);
        Set<ConstraintViolation<BookingInputDto>> violations = validator.validate(bookingInputDto);
        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("start");
    }

    @Test
    void whenBookingInputDtoEndIsNullThenViolationsShouldBeReported() {
        bookingInputDto.setEnd(null);
        Set<ConstraintViolation<BookingInputDto>> violations = validator.validate(bookingInputDto);
        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("end");
    }

    @Test
    void whenBookingInputDtoStartBeforeNowThenViolationsShouldBeReported() {
        bookingInputDto.setStart(LocalDateTime.now().minusSeconds(1));
        Set<ConstraintViolation<BookingInputDto>> violations = validator.validate(bookingInputDto);
        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("start");
    }

    @Test
    void whenBookingInputDtoEndBeforeNowThenViolationsShouldBeReported() {
        bookingInputDto.setEnd(LocalDateTime.now().minusSeconds(1));
        Set<ConstraintViolation<BookingInputDto>> violations = validator.validate(bookingInputDto);
        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("end");
    }
}