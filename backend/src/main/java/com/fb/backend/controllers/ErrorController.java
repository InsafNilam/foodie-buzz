package com.fb.backend.controllers;

import com.fb.backend.domain.dtos.ErrorDTO;
import com.fb.backend.exceptions.BaseException;
import com.fb.backend.exceptions.RestaurantNotFoundException;
import com.fb.backend.exceptions.ReviewNotAllowedException;
import com.fb.backend.exceptions.StorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@ControllerAdvice
@Slf4j
public class ErrorController {
    @ExceptionHandler(ReviewNotAllowedException.class)
    public ResponseEntity<ErrorDTO> reviewNotAllowedException(ReviewNotAllowedException ex) {
        log.error("Caught Review Not Allowed Exception", ex);

        ErrorDTO errorDTO = ErrorDTO.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Review cannot be created or updated")
                .build();

        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RestaurantNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleRestaurantNotFoundException(RestaurantNotFoundException ex) {
        log.error("Caught Restaurant Not Found Exception", ex);

        ErrorDTO errorDTO = ErrorDTO.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message("Restaurant Not Found")
                .build();

        return new ResponseEntity<>(errorDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
        log.error("Caught Method Argument Not Valid Exception", ex);

        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error-> error.getField()+": "+ error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrorDTO errorDTO = ErrorDTO.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(errorMessage)
                .build();

         return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    // Handle storage-related exceptions
    @ExceptionHandler(StorageException.class)
    public ResponseEntity<ErrorDTO> handleStorageException(StorageException e) {
        log.error("Caught StorageException", e);

        ErrorDTO errorDTO = ErrorDTO.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Unable to save or retrieve resources at this time")
                .build();

        return new ResponseEntity<>(errorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Handle our base application exception
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorDTO> handleBaseException(BaseException ex) {
        log.error("Caught BaseException", ex);
        ErrorDTO error = ErrorDTO.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("An unexpected error occurred")
                .build();
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Catch-all for unexpected exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleException(Exception ex) {
        log.error("Caught unexpected exception", ex);
        ErrorDTO error = ErrorDTO.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("An unexpected error occurred")
                .build();
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
