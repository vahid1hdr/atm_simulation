package com.egs.eval.client.service.config;

import com.egs.eval.client.api.dto.ErrorResponse;
import com.egs.eval.client.external.FeignGeneralException;
import com.egs.eval.client.service.exception.AuthenticationFailedException;
import com.egs.eval.client.service.exception.TooManyRequestsException;
import com.egs.eval.client.service.exception.ValidationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
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
@RequiredArgsConstructor
public class GeneralControllerAdvice {

    private final ObjectMapper objectMapper;

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

    @SneakyThrows
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse validationExceptionHandler(ValidationException e){
        return objectMapper.readValue(e.getMessage(), ErrorResponse.class);
    }

    @SneakyThrows
    @ExceptionHandler(AuthenticationFailedException.class)
    @ResponseStatus(HttpStatus.PROXY_AUTHENTICATION_REQUIRED)
    @ResponseBody
    public ErrorResponse authenticationFailedExceptionHandler(AuthenticationFailedException e){
        String message = Strings.isBlank(e.getMessage()) ? "{}" : e.getMessage();
        return objectMapper.readValue(message, ErrorResponse.class);
    }

    @SneakyThrows
    @ExceptionHandler(TooManyRequestsException.class)
    @ResponseStatus(HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS)
    @ResponseBody
    public ErrorResponse tooManyRequestsExceptionHandler(TooManyRequestsException e){
        return objectMapper.readValue(e.getMessage(), ErrorResponse.class);
    }

    @ExceptionHandler(FeignGeneralException.ServiceUnavailable.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ResponseBody
    public ErrorResponse serviceUnavailableExceptionHandler(FeignGeneralException.ServiceUnavailable e){
        log.warn("core bank is unavailable", e);
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
