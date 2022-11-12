package com.egs.eval.client.external.util;

import com.egs.eval.client.external.FeignGeneralException;
import com.egs.eval.client.service.exception.AuthenticationFailedException;
import com.egs.eval.client.service.exception.TooManyRequestsException;
import com.egs.eval.client.service.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.util.Objects;

@Slf4j
public class ExceptionConverter {

    private ExceptionConverter() {
        throw new IllegalStateException("Utility class");
    }

    public static RuntimeException getProperExceptionFromFeignGeneralException(FeignGeneralException e) {
        log.warn("core bank call exception occured", e);
        HttpStatus status = HttpStatus.resolve(e.getStatus());
        if (Objects.isNull(status))
            return e;
        return switch (status) {
            case UNAUTHORIZED -> new AuthenticationFailedException(e.getBody());
            case TOO_MANY_REQUESTS -> new TooManyRequestsException(e.getBody());
            case PRECONDITION_FAILED, CONFLICT, BAD_REQUEST -> new ValidationException(e.getBody());
            case NOT_FOUND -> new RuntimeException(e.getBody());
            default -> new FeignGeneralException(e.getStatus(), e.getBody());
        };
    }

}
