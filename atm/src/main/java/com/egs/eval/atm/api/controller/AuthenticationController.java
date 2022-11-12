package com.egs.eval.atm.api.controller;

import com.egs.eval.atm.api.dto.AuthRequest;
import com.egs.eval.atm.api.dto.AuthTypesResponse;
import com.egs.eval.atm.api.dto.TokenResponse;
import com.egs.eval.atm.api.facade.AuthenticationFacade;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public/v1/auth")
@Api(tags = "authentication apis")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationFacade authenticationFacade;

    @GetMapping("/types")
    public AuthTypesResponse getAuthenticationsMechanisms() {
        return authenticationFacade.getAuthMechanisms();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TokenResponse authenticate(@Validated @RequestBody AuthRequest authRequest) {
        return authenticationFacade.authenticate(authRequest);
    }
}
