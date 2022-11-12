package com.egs.eval.atm.api.facade;

import com.egs.eval.atm.api.dto.AuthRequest;
import com.egs.eval.atm.api.dto.AuthTypesResponse;
import com.egs.eval.atm.api.dto.TokenResponse;
import com.egs.eval.atm.service.model.AuthenticationMechanism;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthenticationFacade {

    private final AuthenticationFactory authFactory;

    public TokenResponse authenticate(AuthRequest authRequest) {
        String token = authFactory.getAuthMechanism(authRequest.getAuthType())
                .authenticate(authRequest.getCardNumber(), authRequest.getValue());
        return new TokenResponse(token);
    }

    public AuthTypesResponse getAuthMechanisms() {
        List<String> mechanisms = Arrays.stream(AuthenticationMechanism.values())
                .map(Enum::name)
                .toList();
        return new AuthTypesResponse(mechanisms);
    }
}
