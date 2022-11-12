package com.egs.eval.client.api.controller;

import com.egs.eval.client.external.model.AuthRequest;
import com.egs.eval.client.external.model.AuthTypesResponse;
import com.egs.eval.client.external.model.TokenResponse;
import com.egs.eval.client.service.AuthProxy;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public/v1/auth")
@Api(tags = "authentication apis")
@RequiredArgsConstructor
public class AuthController {

    private final AuthProxy authProxy;

    @GetMapping("/types")
    AuthTypesResponse getAuthTypes() {
        return authProxy.getAuthTypes();
    }

    @PostMapping
    TokenResponse getToken(@RequestBody AuthRequest authRequest) {
        return authProxy.getFreshToken(authRequest);
    }
}
