package com.egs.eval.client.service;

import com.egs.eval.client.external.BankAuthApiClient;
import com.egs.eval.client.external.FeignGeneralException;
import com.egs.eval.client.external.model.AuthRequest;
import com.egs.eval.client.external.model.AuthTypesResponse;
import com.egs.eval.client.external.model.TokenResponse;
import com.egs.eval.client.external.util.ExceptionConverter;
import com.egs.eval.client.service.exception.TooManyRequestsException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SimpleAuthProxy implements AuthProxy {

    private final BankAuthApiClient bankAuthApiClient;

    @Override
    public AuthTypesResponse getAuthTypes() {
        return bankAuthApiClient.getAuthMechanisms();
    }

    @CircuitBreaker(name = "bank-auth", fallbackMethod = "getFreshTokenFallBack")
    @Override
    public TokenResponse getFreshToken(AuthRequest authRequest) {
        return bankAuthApiClient.getToken(authRequest);
    }
    private TokenResponse getFreshTokenFallBack(AuthRequest authRequest, Throwable throwable){
        RuntimeException exception = getProperExceptionFromCoreBankException(throwable);
        if (exception instanceof TooManyRequestsException){
            log.info("card has been locked.");
        }
        throw exception;
    }

    private RuntimeException getProperExceptionFromCoreBankException(Throwable throwable) {
        if (throwable.getClass().equals(FeignGeneralException.class)){
            return ExceptionConverter.getProperExceptionFromFeignGeneralException((FeignGeneralException) throwable);
        }else{
            return new FeignGeneralException.ServiceUnavailable("server error. please try after 30 seconds!");
        }
    }
}
