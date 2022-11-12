package com.egs.eval.atm.service.exception;

public class NumberOfAttemptsExceededException extends RuntimeException {
    public NumberOfAttemptsExceededException(String message) {
        super(message);
    }
}
