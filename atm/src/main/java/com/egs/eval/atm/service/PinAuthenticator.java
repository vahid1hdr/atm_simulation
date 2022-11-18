package com.egs.eval.atm.service;

import com.egs.eval.atm.dal.entity.User;
import com.egs.eval.atm.service.model.AuthenticationMechanism;
import com.egs.eval.atm.service.model.UserQueryModel;
import org.springframework.stereotype.Component;

@Component
public class PinAuthenticator implements Authenticator {

    private final UserService userService;
    private final TokenGranter tokenGranter;

    public PinAuthenticator(UserService userService, TokenGranter tokenGranter) {
        this.userService = userService;
        this.tokenGranter = tokenGranter;
    }

    @Override
    public String authenticate(String cardNumber, String pin) {
        UserQueryModel queryModel = UserQueryModel.builder().card(cardNumber).pin(pin).build();
        User user = userService.getUserByQueryModel(queryModel);
        return tokenGranter.generateToken(user.getId().toString());
    }

    @Override
    public boolean matches(AuthenticationMechanism authenticationMechanism) {
        return AuthenticationMechanism.PIN.equals(authenticationMechanism);
    }

}
