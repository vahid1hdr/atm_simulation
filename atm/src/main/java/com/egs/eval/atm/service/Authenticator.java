package com.egs.eval.atm.service;

import com.egs.eval.atm.service.exception.NotAuthenticatedException;
import com.egs.eval.atm.service.exception.NumberOfAttemptsExceededException;
import com.egs.eval.atm.service.model.AuthenticationMechanism;
import com.egs.eval.atm.service.model.FailureModel;

import java.util.function.Supplier;

public interface Authenticator {
    String authenticate(String cardNumber, String input);

    boolean matches(AuthenticationMechanism authenticationMechanism);

    default RuntimeException doFailureWithMessageSupply(Supplier<FailureModel> failureSupplier) {
        FailureModel failureModel = failureSupplier.get();
        if (failureModel.getNumberOfAttempts() > 3)
            return new NumberOfAttemptsExceededException(failureModel.getMessage());
        return new NotAuthenticatedException(failureModel.getMessage());
    }
}
