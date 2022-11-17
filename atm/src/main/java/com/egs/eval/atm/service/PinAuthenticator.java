package com.egs.eval.atm.service;

import com.egs.eval.atm.service.model.AuthenticationMechanism;
import com.egs.eval.atm.service.model.FailureModel;
import com.egs.eval.atm.service.model.UserQueryModel;
import org.springframework.stereotype.Component;

@Component
public class PinAuthenticator implements Authenticator {

    private final UserService userService;
    private final TokenGranter tokenGranter;
//    @Value("${max.allowed.failed.attempts}")
    private int maxAllowedFailedAttempts=3;

    public PinAuthenticator(UserService userService, TokenGranter tokenGranter) {
        this.userService = userService;
        this.tokenGranter = tokenGranter;
    }

    @Override
    public String authenticate(String cardNumber, String pin) {
        UserQueryModel queryModel = UserQueryModel.builder().card(cardNumber).pin(pin).build();
        return userService.getUserByCardNumber(queryModel)
                .map(user -> tokenGranter.generateToken(user.getId().toString()))
                .orElseThrow(() -> doFailureProcess(queryModel.getCard()));
    }

    @Override
    public boolean matches(AuthenticationMechanism authenticationMechanism) {
        return AuthenticationMechanism.PIN.equals(authenticationMechanism);
    }

    private RuntimeException doFailureProcess(String cardNo) {
        int attempts = userService.addTodayFailedLoginAttempts(cardNo);
        return doFailureWithMessageSupply(
                () -> new FailureModel(attempts, "Authentication via pin failed. number of today attempts: " + attempts)
        );
    }

}
