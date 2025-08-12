package org.AlanTitor.MusicApp.Exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.AlanTitor.MusicApp.Dto.Exception.ErrorResponseDto;
import org.AlanTitor.MusicApp.Exception.CustomExceptions.MusicNotFoundException;
import org.AlanTitor.MusicApp.Exception.CustomExceptions.UserDuplicateException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.io.IOException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<?> handleAllExceptions(Exception exception){
//        ErrorResponseDto error = new ErrorResponseDto(
//                "Internal Server Error",
//                "Unexpected error happened!",
//                HttpStatus.INTERNAL_SERVER_ERROR.value()
//        );
//        return ResponseEntity.internalServerError().body(error);
//    }

    // if one part of requested param isn't presented in request
    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFoundException(MissingServletRequestPartException exception){
        ErrorResponseDto error = new ErrorResponseDto(
                "Bad request",
                "Required request part is missing.",
                HttpStatus.BAD_REQUEST.value()
        );
        return ResponseEntity.badRequest().body(error);
    }

    // if param doesn't exits
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponseDto> handleMissingRequestParamException(MissingServletRequestParameterException exception){
        String errorMessage = "Field %s is not presented.".formatted(exception.getParameterName());

        ErrorResponseDto error = new ErrorResponseDto(
                "Bad request",
                errorMessage,
                HttpStatus.BAD_REQUEST.value()
        );
        return ResponseEntity.badRequest().body(error);
    }

    // if violated validation rules
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationException(ConstraintViolationException exception){
        String errorMessage = exception.getConstraintViolations().stream().map(ConstraintViolation::getMessageTemplate).collect(Collectors.joining(" "));

        ErrorResponseDto error = new ErrorResponseDto(
                "Bad request",
                errorMessage,
                HttpStatus.BAD_REQUEST.value()
        );
        return ResponseEntity.badRequest().body(error);
    }

    // if music in DB is absent
    @ExceptionHandler(MusicNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleMusicNotFound(){
        ErrorResponseDto error = new ErrorResponseDto(
                "Not found",
                "Can't find music in db.",
                HttpStatus.NOT_FOUND.value()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // if file in File System is absent
    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponseDto> handleMusicIOEx(IOException exception){
        ErrorResponseDto error = new ErrorResponseDto(
                "Internal server error",
                "Can't find music in file system.",
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return ResponseEntity.internalServerError().body(error);
    }

    // if user have no permission
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponseDto> handleAuthorizationEx(AuthorizationDeniedException exception){
        ErrorResponseDto error = new ErrorResponseDto(
                "Internal server error",
                exception.getMessage(),
                HttpStatus.UNAUTHORIZED.value()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    // If error in data validation
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationErrors(MethodArgumentNotValidException exception){
        String errorMessage = exception.getBindingResult().getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(" "));

        ErrorResponseDto error = new ErrorResponseDto(
                "Bad request",
                errorMessage,
                HttpStatus.BAD_REQUEST.value()
        );
        return ResponseEntity.badRequest().body(error);
    }

    // if usr already exists in db
    @ExceptionHandler(UserDuplicateException.class)
    public ResponseEntity<ErrorResponseDto> handleUserDuplication(UserDuplicateException exception){
        ErrorResponseDto error = new ErrorResponseDto(
                "Bad request",
                "User already exists.",
                HttpStatus.CONFLICT.value()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
}
