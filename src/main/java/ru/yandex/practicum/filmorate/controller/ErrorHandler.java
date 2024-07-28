package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(ValidationException e) {
        log.error(e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException e) {
        log.error(e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {
        List<ErrorResponse> errors = new ArrayList<>();

        e.getConstraintViolations().forEach(violation -> {
            String fieldName = ((PathImpl) violation.getPropertyPath()).getLeafNode().asString();
            errors.add(new ErrorResponse(String.format(
                    "Not valid field %s: %s", fieldName, violation.getMessage())));
        });

        errors.stream()
                .forEach(error -> log.error(error.getError()));
        return errors;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<ErrorResponse> errors = new ArrayList<>();

        e.getBindingResult().getFieldErrors()
                .forEach(fieldError ->
                        errors.add(new ErrorResponse(
                                String.format(
                                        "Not valid field %s: %s",
                                        fieldError.getField(),
                                        fieldError.getDefaultMessage())
                        )));

        errors.stream()
                .forEach(error -> log.error(error.getError()));
        return errors;
    }
}