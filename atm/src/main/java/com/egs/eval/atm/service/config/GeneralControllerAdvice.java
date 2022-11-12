package com.egs.eval.atm.service.config;

import com.egs.eval.atm.api.dto.ErrorResponse;
import com.egs.eval.atm.service.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Slf4j
@ControllerAdvice
public class GeneralControllerAdvice {
    // the same
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody
    ErrorResponse methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        log.warn("methodArgumentNotValidExceptionHandler", e);
        Set<ErrorResponse.ValidationError> validationErrorSet = new HashSet<>();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            if (!validationErrorSet.add(
                    ErrorResponse.ValidationError.builder()
                            .field(fieldError.getField())
                            .message(fieldError.getDefaultMessage() == null ? fieldError.getCode() : fieldError.getDefaultMessage())
                            .build()
            )) {
                throw new IllegalStateException("Duplicate key");
            }
        }
        return ErrorResponse.builder().errors(validationErrorSet).build();
    }
// the same
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody
    ErrorResponse bindExceptionHandler(BindException e) {
        log.warn("bindExceptionHandler", e);
        Set<ErrorResponse.ValidationError> errorSet = new HashSet<>();
        e.getBindingResult().getFieldErrors().forEach(fieldError -> {
            if (!errorSet.add(
                    ErrorResponse.ValidationError.builder()
                            .field(fieldError.getField())
                            .message(fieldError.getDefaultMessage() == null ? fieldError.getCode() : fieldError.getDefaultMessage())
                            .build()
            )) {
                throw new IllegalStateException("Duplicate key");
            }
        });
        return ErrorResponse.builder().errors(errorSet).build();

    }

    @ExceptionHandler(NotAuthenticatedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ErrorResponse notAuthenticatedExceptionHandler(NotAuthenticatedException e) {
        log.warn("notAuthenticatedExceptionHandler", e);
        return ErrorResponse.builder().message(e.getMessage()).build();
    }

    @ExceptionHandler(NumberOfAttemptsExceededException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    @ResponseBody
    public ErrorResponse numberOfAttemptsExceededExceptionHandler(NumberOfAttemptsExceededException e) {
        log.warn("numberOfAttemptsExceededException", e);
        return ErrorResponse.builder().message(e.getMessage()).build();
    }

    @ExceptionHandler(PreconditionFailedException.class)
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    @ResponseBody
    public ErrorResponse preconditionFailedExceptionHandler(PreconditionFailedException e){
        log.warn("PreconditionFailedException", e);
        return ErrorResponse.builder().message(e.getMessage()).build();
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorResponse conflictExceptionHandler(ConflictException e){
        log.warn("ConflictException", e);
        return ErrorResponse.builder().message(e.getMessage()).build();
    }
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse notFoundExceptionHandler(NotFoundException e){
        log.warn("NotFoundException", e);
        return ErrorResponse.builder().message(e.getMessage()).build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse generalErrorHandler(Exception e) {
        String fingerPrint = UUID.randomUUID().toString();
        log.error("UnpredictedException: fingerprint = {}", fingerPrint, e);
        return ErrorResponse.builder().logFingerPrint(fingerPrint).build();
    }
}

