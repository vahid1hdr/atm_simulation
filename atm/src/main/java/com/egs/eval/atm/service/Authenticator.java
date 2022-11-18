package com.egs.eval.atm.service;

import com.egs.eval.atm.service.model.AuthenticationMechanism;

public interface Authenticator {
    String authenticate(String cardNumber, String input);

    boolean matches(AuthenticationMechanism authenticationMechanism);
}
